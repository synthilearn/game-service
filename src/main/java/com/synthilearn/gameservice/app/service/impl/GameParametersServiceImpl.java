package com.synthilearn.gameservice.app.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.synthilearn.gameservice.GameServiceApplication;
import com.synthilearn.gameservice.app.service.GameParametersService;
import com.synthilearn.gameservice.domain.GameParameters;
import com.synthilearn.gameservice.domain.mapper.DomainMapper;
import com.synthilearn.gameservice.infra.adapter.dto.AllPhraseRequestDto;
import com.synthilearn.gameservice.infra.persistence.jpa.entity.GameParametersEntity;
import com.synthilearn.gameservice.infra.persistence.jpa.repository.GameJpaRepository;
import com.synthilearn.gameservice.infra.persistence.jpa.repository.GameParametersJpaRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GameParametersServiceImpl implements GameParametersService {

    private final GameParametersJpaRepository gameParametersJpaRepository;
    private final GameJpaRepository gameJpaRepository;
    private final DomainMapper domainMapper;
    private final GameServiceApplication gameServiceApplication;

    @Transactional
    @Override
    public Mono<Void> initParameters(AllPhraseRequestDto requestDto, UUID gameId) {
        return gameParametersJpaRepository.findById(gameId)
                .singleOptional()
                .flatMap(parameters -> {
                    parameters.ifPresentOrElse(parameter ->
                                    init(requestDto, gameId).toBuilder().newRecord(false).build(),
                            () -> gameParametersJpaRepository.save(init(requestDto, gameId))
                                    .subscribe());
                    return Mono.empty();
                });
    }

    @Override
    public Mono<GameParameters> getGameParameters(UUID workareaId) {
        return gameJpaRepository.findTopByOrderByCreationDateDesc(workareaId)
                .singleOptional()
                .flatMap(game -> game.map(
                                entity -> gameParametersJpaRepository.findById(entity.getId()))
                        .orElseGet(Mono::empty))
                .map(domainMapper::map);
    }

    private GameParametersEntity init(AllPhraseRequestDto requestDto, UUID gameId) {
        return GameParametersEntity.builder()
                .newRecord(true)
                .phraseTypes(requestDto.getPhraseTypes().toString())
                .availablePartOfSpeech(requestDto.getPartsOfSpeech().toString())
                .dateFrom(requestDto.getDateFrom())
                .dateTo(requestDto.getDateTo())
                .typeOfGame(requestDto.getTypeOfGame())
                .timeOnWord(requestDto.getTimeOnWord())
                .translatesAmount(requestDto.getTranslatesAmount())
                .id(gameId)
                .build();
    }
}
