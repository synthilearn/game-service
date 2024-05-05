package com.synthilearn.gameservice.app.service;

import java.util.UUID;

import com.synthilearn.gameservice.domain.Game;
import com.synthilearn.gameservice.infra.adapter.dto.AllPhraseRequestDto;
import com.synthilearn.gameservice.infra.rest.dto.CurrentGameResponseDto;

import reactor.core.publisher.Mono;

public interface GameService {

    Mono<Game> startGame(AllPhraseRequestDto requestDto, UUID workareaId);
    Mono<CurrentGameResponseDto> getCurrentGame(UUID workareaId);
}
