package com.synthilearn.gameservice.app.service;

import java.util.UUID;

import com.synthilearn.gameservice.domain.GameStatistic;

import reactor.core.publisher.Mono;

public interface GameStatisticService {
    Mono<GameStatistic> getStatistic(UUID id);
}
