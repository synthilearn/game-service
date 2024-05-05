package com.synthilearn.gameservice.app.service.impl;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.synthilearn.gameservice.app.service.GameParametersService;
import com.synthilearn.gameservice.app.service.GameService;
import com.synthilearn.gameservice.app.service.TranslateInGameService;
import com.synthilearn.gameservice.domain.Game;
import com.synthilearn.gameservice.domain.GameStatus;
import com.synthilearn.gameservice.domain.mapper.DomainMapper;
import com.synthilearn.gameservice.infra.adapter.dto.AllPhraseRequestDto;
import com.synthilearn.gameservice.infra.persistence.jpa.entity.GameEntity;
import com.synthilearn.gameservice.infra.persistence.jpa.entity.TranslateInGameEntity;
import com.synthilearn.gameservice.infra.persistence.jpa.repository.GameJpaRepository;
import com.synthilearn.gameservice.infra.persistence.jpa.repository.TranslateInGameJpaRepository;
import com.synthilearn.gameservice.infra.rest.dto.CurrentGameResponseDto;
import com.synthilearn.gameservice.infra.rest.exception.GameException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final GameJpaRepository gameJpaRepository;
    private final TranslateInGameService translateInGameService;
    private final TranslateInGameJpaRepository translateInGameJpaRepository;
    private final GameParametersService gameParametersService;
    private final DomainMapper domainMapper;

    @Override
    @Transactional
    public Mono<Game> startGame(AllPhraseRequestDto requestDto, UUID workareaId) {
        UUID gameId = UUID.randomUUID();
        return translateInGameService.defineTranslatesInGame(requestDto, gameId)
                .flatMap(phrases -> gameJpaRepository.save(
                                initGame(phrases, workareaId, gameId, requestDto))
                        .flatMap(game -> gameParametersService.initParameters(requestDto,
                                        gameId)
                                .thenReturn(domainMapper.map(game))));
    }

    @Override
    public Mono<CurrentGameResponseDto> getCurrentGame(UUID workareaId) {
        return gameJpaRepository.findTopByOrderByCreationDateDesc(workareaId)
                .switchIfEmpty(Mono.error(GameException.notFound(workareaId)))
                .flatMap(game -> translateInGameJpaRepository.findAllByGameId(game.getId())
                        .collectList()
                        .map(translates -> {
                            ZonedDateTime now = ZonedDateTime.now();
                            long secondsFromStartGame =
                                    game.getCreationDate().until(now, ChronoUnit.SECONDS);

                            if (secondsFromStartGame <= 0) {
                                return new CurrentGameResponseDto(game.getCreationDate(), false);
                            }

                            int currentStage = (int) Math.ceil((double) secondsFromStartGame / 10);
                            int allStages = game.getPhrasesInGame().size();

                            if (currentStage > allStages) {
                                return new CurrentGameResponseDto(true);
                            }

                            String currentPhrase = game.getPhrasesInGame().get(currentStage - 1)
                                    .replace("{", "").replace("}", "");
                            List<String> translatesForStage = translates.stream()
                                    .filter(translation -> translation.getQuestion() ==
                                            currentStage)
                                    .map(TranslateInGameEntity::getTranslateText)
                                    .toList();
                            ZonedDateTime endStage = game.getCreationDate()
                                    .plusSeconds((long) currentStage * game.getTimeOnWord());

                            return new CurrentGameResponseDto(currentPhrase, currentStage,
                                    allStages, endStage,
                                    game.getCreationDate(),
                                    translatesForStage, true, false);
                        }));
    }

    private GameEntity initGame(List<String> phrases, UUID workareaId, UUID gameId,
                                AllPhraseRequestDto requestDto) {
        return GameEntity.builder()
                .newRecord(true)
                .id(gameId)
                .timeOnWord(requestDto.getTimeOnWord())
                .phrasesInGame(phrases)
                .statisticCreated(false)
                .workareaId(workareaId)
                .creationDate(ZonedDateTime.now().plusSeconds(10))
                .status(GameStatus.IN_PROGRESS)
                .build();
    }
}
