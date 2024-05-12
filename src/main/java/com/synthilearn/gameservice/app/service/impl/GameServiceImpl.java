package com.synthilearn.gameservice.app.service.impl;

import java.awt.print.Pageable;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.synthilearn.gameservice.app.service.GameParametersService;
import com.synthilearn.gameservice.app.service.GameService;
import com.synthilearn.gameservice.app.service.TranslateInGameService;
import com.synthilearn.gameservice.domain.Game;
import com.synthilearn.gameservice.domain.GameStatus;
import com.synthilearn.gameservice.domain.TranslateInGame;
import com.synthilearn.gameservice.domain.mapper.DomainMapper;
import com.synthilearn.gameservice.infra.adapter.dto.AllPhraseRequestDto;
import com.synthilearn.gameservice.infra.persistence.jpa.entity.GameEntity;
import com.synthilearn.gameservice.infra.persistence.jpa.entity.TranslateInGameEntity;
import com.synthilearn.gameservice.infra.persistence.jpa.repository.GameJpaRepository;
import com.synthilearn.gameservice.infra.persistence.jpa.repository.GameParametersJpaRepository;
import com.synthilearn.gameservice.infra.persistence.jpa.repository.TranslateInGameJpaRepository;
import com.synthilearn.gameservice.infra.rest.dto.AllGamesResultDto;
import com.synthilearn.gameservice.infra.rest.dto.AnswerRequestDto;
import com.synthilearn.gameservice.infra.rest.dto.AnswerResponseDto;
import com.synthilearn.gameservice.infra.rest.dto.CurrentGameResponseDto;
import com.synthilearn.gameservice.infra.rest.dto.CurrentStageInfo;
import com.synthilearn.gameservice.infra.rest.dto.GameStateDto;
import com.synthilearn.gameservice.infra.rest.dto.GetAllGamesRequest;
import com.synthilearn.gameservice.infra.rest.exception.GameException;
import com.synthilearn.gameservice.infra.rest.exception.StageException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final GameJpaRepository gameJpaRepository;
    private final TranslateInGameService translateInGameService;
    private final TranslateInGameJpaRepository translateInGameJpaRepository;
    private final GameParametersService gameParametersService;
    private final DomainMapper domainMapper;
    private final GameParametersJpaRepository gameParametersJpaRepository;

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
                .flatMap(game -> gameParametersService.getGameParameters(workareaId)
                        .flatMap(parameters -> translateInGameJpaRepository.findAllByGameId(
                                        game.getId())
                                .collectList()
                                .flatMap(translates -> {
                                    ZonedDateTime now = ZonedDateTime.now();
                                    long secondsFromStartGame =
                                            game.getCreationDate().until(now, ChronoUnit.SECONDS);

                                    if (secondsFromStartGame <= 0) {
                                        return Mono.just(
                                                new CurrentGameResponseDto(game.getCreationDate(),
                                                        GameStateDto.NOT_STARTED));
                                    }

                                    CurrentStageInfo stageInfo =
                                            getCurrentStageInfo(game, translates,
                                                    parameters.getTimeOnWord());

                                    if (stageInfo == null) {
                                        game.setStatus(GameStatus.GENERATE_STATISTIC);
                                        return gameJpaRepository.save(game)
                                                .flatMap(x -> Mono.just(new CurrentGameResponseDto(
                                                        GameStateDto.FINISHED)));
                                    }

                                    log.info(
                                            "Этап: {}. Текущее время: {}:{} окончание этапа в: {}:{}. Игра: {}",
                                            stageInfo.getStage(),
                                            now.getMinute(),
                                            now.getSecond(),
                                            stageInfo.getEndStageTime().getMinute(),
                                            stageInfo.getEndStageTime().getSecond(), game.getId());

                                    int allStages = game.getPhrasesInGame().size();

                                    String currentPhrase =
                                            game.getPhrasesInGame().get(stageInfo.getStage() - 1)
                                                    .replace("{", "").replace("}", "");
                                    List<String> translatesForStage = translates.stream()
                                            .filter(translation -> Objects.equals(
                                                    translation.getQuestion(),
                                                    stageInfo.getStage()))
                                            .map(TranslateInGameEntity::getTranslateText)
                                            .toList();

                                    return Mono.just(new CurrentGameResponseDto(currentPhrase,
                                            stageInfo.getStage(),
                                            allStages, stageInfo.getEndStageTime(),
                                            game.getCreationDate(),
                                            translatesForStage, GameStateDto.IN_PROGRESS,
                                            game.getId()));
                                })));
    }

    private CurrentStageInfo getCurrentStageInfo(GameEntity game,
                                                 List<TranslateInGameEntity> translates,
                                                 Integer timeOnWord) {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime startTimeStage = game.getCreationDate();
        int allStages = game.getPhrasesInGame().size();
        int stage = 0;

        while (startTimeStage.isBefore(now) && ++stage <= allStages) {
            int finalStage = stage;
            startTimeStage = translates.stream()
                    .filter(translate ->
                            translate.getAnswerTime() != null &&
                                    translate.getQuestion() == finalStage)
                    .map(TranslateInGameEntity::getAnswerTime)
                    .findFirst()
                    .orElse(startTimeStage.plusSeconds(timeOnWord + 2));
        }
        return stage > allStages ? null :
                new CurrentStageInfo(stage, startTimeStage);
    }

    @Override
    @Transactional
    public Mono<AnswerResponseDto> answerQuestion(AnswerRequestDto request) {
        return getCurrentGame(request.getWorkareaId())
                .flatMap(translates -> {
                    if (!Objects.equals(translates.getCurrentStage(), request.getStage())) {
                        return Mono.error(StageException.missMatch(translates.getCurrentStage(),
                                request.getStage(), request.getGameId()));
                    }

                    String translate = getTranslateOrDie(translates.getAnswerOptions(), request);

                    return translateInGameJpaRepository.findByTranslateTextAndGameIdAndQuestion(
                                    translate, request.getGameId(), request.getStage())
                            .map(domainMapper::map);
                }).flatMap(translateInGame -> {
                    translateInGame.setAnswer(true);
                    translateInGame.setAnswerTime(ZonedDateTime.now());
                    return translateInGameJpaRepository.save(domainMapper.map(translateInGame))
                            .map(domainMapper::map);
                }).flatMap(newTranslateInGame -> formResponse(newTranslateInGame,
                        request.getGameId()));
    }

    @Override
    public Mono<AllGamesResultDto> getAll(GetAllGamesRequest request) {
        return gameJpaRepository.findAllByWorkareaId(request.getWorkareaId(),
                        PageRequest.of(request.getPage(), request.getSize()))
                .collectList()
                .map(games -> games.stream().map(domainMapper::map).toList())
                .zipWith(gameJpaRepository.countAllByWorkareaId(request.getWorkareaId()))
                .map(el -> new AllGamesResultDto(el.getT1(),
                        (int) Math.ceil((double) el.getT2() / request.getSize())));
    }

    @Override
    public Mono<Game> getOne(UUID id) {
        return gameJpaRepository.findById(id)
                .map(domainMapper::map);
    }

    private Mono<AnswerResponseDto> formResponse(TranslateInGame newTranslateInGame, UUID gameId) {
        return translateInGameJpaRepository.findByGameIdAndQuestionAndCorrect(
                gameId, newTranslateInGame.getQuestion(),
                true).flatMap(correctTranslate -> Mono.just(
                new AnswerResponseDto(newTranslateInGame.getCorrect(),
                        correctTranslate.getTranslateText())));
    }

    private String getTranslateOrDie(List<String> translates, AnswerRequestDto request) {
        return translates
                .stream().filter(translate -> request.getTranslate().equals(translate))
                .findFirst()
                .orElseThrow(() -> GameException.translateNotFound(request.getTranslate(),
                        translates,
                        request.getGameId()));
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
                .creationDate(ZonedDateTime.now().plusSeconds(5))
                .status(GameStatus.IN_PROGRESS)
                .build();
    }
}
