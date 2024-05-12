package com.synthilearn.gameservice.infra.persistence.jpa.repository;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.synthilearn.gameservice.infra.persistence.jpa.entity.GameEntity;
import com.synthilearn.gameservice.infra.persistence.jpa.entity.GameStatisticEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface GameStatisticJpaRepository extends ReactiveCrudRepository<GameStatisticEntity, UUID> {

}
