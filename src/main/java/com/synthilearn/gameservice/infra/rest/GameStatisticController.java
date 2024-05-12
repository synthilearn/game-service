package com.synthilearn.gameservice.infra.rest;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.synthilearn.commonstarter.GenericResponse;
import com.synthilearn.gameservice.app.service.GameStatisticService;
import com.synthilearn.gameservice.domain.GameStatistic;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/game-service/v1/statistic")
public class GameStatisticController {

    private final GameStatisticService gameStatisticService;

    @GetMapping("/{id}")
    public Mono<GenericResponse<GameStatistic>> getGameStatistic(@PathVariable UUID id) {
        return gameStatisticService.getStatistic(id)
                .map(GenericResponse::ok);
    }
}
