package com.synthilearn.gameservice.app.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.synthilearn.gameservice.app.service.GameStatisticService;
import com.synthilearn.gameservice.domain.GameStatistic;
import com.synthilearn.gameservice.domain.mapper.DomainMapper;
import com.synthilearn.gameservice.infra.persistence.jpa.repository.GameStatisticJpaRepository;
import com.synthilearn.gameservice.infra.rest.exception.GameStatisticException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GameStatisticServiceImpl implements GameStatisticService {

    private final GameStatisticJpaRepository gameStatisticJpaRepository;
    private final DomainMapper domainMapper;

    public Mono<GameStatistic> getStatistic(UUID id) {
        return gameStatisticJpaRepository.findById(id)
                .switchIfEmpty(Mono.error(GameStatisticException.notFound(id)))
                .map(domainMapper::map);
    }
}
