package com.synthilearn.gameservice.infra.rest;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.synthilearn.commonstarter.GenericResponse;
import com.synthilearn.gameservice.app.service.GameParametersService;
import com.synthilearn.gameservice.domain.GameParameters;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/game-service/v1/parameter")
public class GameParametersController {

    private final GameParametersService gameParametersService;

    @GetMapping
    public Mono<GenericResponse<GameParameters>> getGameParameters(@RequestParam("workarea_id")
                                                                   UUID workareaId) {
        return gameParametersService.getGameParameters(workareaId)
                .map(GenericResponse::ok);
    }
}
