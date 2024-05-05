package com.synthilearn.gameservice.app.service;

import java.util.UUID;

import com.synthilearn.gameservice.domain.GameParameters;
import com.synthilearn.gameservice.infra.adapter.dto.AllPhraseRequestDto;

import reactor.core.publisher.Mono;

public interface GameParametersService {
    Mono<Void> initParameters(AllPhraseRequestDto requestDto, UUID gameId);
    Mono<GameParameters> getGameParameters(UUID workareaId);
}
