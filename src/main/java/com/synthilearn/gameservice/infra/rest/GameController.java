package com.synthilearn.gameservice.infra.rest;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.synthilearn.commonstarter.GenericResponse;
import com.synthilearn.gameservice.app.service.GameService;
import com.synthilearn.gameservice.domain.Game;
import com.synthilearn.gameservice.infra.adapter.dto.AllPhraseRequestDto;
import com.synthilearn.gameservice.infra.rest.dto.AllGamesResultDto;
import com.synthilearn.gameservice.infra.rest.dto.AnswerResponseDto;
import com.synthilearn.gameservice.infra.rest.dto.AnswerRequestDto;
import com.synthilearn.gameservice.infra.rest.dto.CurrentGameResponseDto;
import com.synthilearn.gameservice.infra.rest.dto.GetAllGamesRequest;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/game-service/v1/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping
    public Mono<GenericResponse<Game>> startGame(@RequestBody AllPhraseRequestDto requestDto,
                                                 @RequestParam("workarea_id") UUID workareaId) {
        return gameService.startGame(requestDto, workareaId)
                .map(GenericResponse::ok);
    }

    @GetMapping
    public Mono<GenericResponse<CurrentGameResponseDto>> getCurrentGame(
            @RequestParam("workarea_id") UUID workareaId) {
        return gameService.getCurrentGame(workareaId)
                .map(GenericResponse::ok);
    }

    @PostMapping("/answer")
    public Mono<GenericResponse<AnswerResponseDto>> answer(
            @Valid @RequestBody AnswerRequestDto request) {
        return gameService.answerQuestion(request)
                .map(GenericResponse::ok);
    }

    @PostMapping("/all")
    public Mono<GenericResponse<AllGamesResultDto>> getAll(
            @Valid @RequestBody GetAllGamesRequest request) {
        return gameService.getAll(request)
                .map(GenericResponse::ok);
    }
}
