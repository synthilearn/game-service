package com.synthilearn.gameservice.app.service;

import java.util.List;
import java.util.UUID;

import com.synthilearn.gameservice.domain.Game;
import com.synthilearn.gameservice.domain.mapper.DomainMapper;
import com.synthilearn.gameservice.infra.adapter.dto.AllPhraseRequestDto;
import com.synthilearn.gameservice.infra.rest.dto.AllGamesResultDto;
import com.synthilearn.gameservice.infra.rest.dto.AnswerResponseDto;
import com.synthilearn.gameservice.infra.rest.dto.AnswerRequestDto;
import com.synthilearn.gameservice.infra.rest.dto.CurrentGameResponseDto;
import com.synthilearn.gameservice.infra.rest.dto.GameResponseDto;
import com.synthilearn.gameservice.infra.rest.dto.GetAllGamesRequest;

import reactor.core.publisher.Mono;

public interface GameService {

    Mono<Game> startGame(AllPhraseRequestDto requestDto, UUID workareaId);

    Mono<CurrentGameResponseDto> getCurrentGame(UUID workareaId);

    Mono<AnswerResponseDto> answerQuestion(AnswerRequestDto request);

    Mono<AllGamesResultDto> getAll(GetAllGamesRequest request);

    Mono<GameResponseDto> getOne(UUID id);
}
