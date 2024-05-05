package com.synthilearn.gameservice.infra.persistence.jpa.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.synthilearn.gameservice.infra.persistence.jpa.entity.GameParametersEntity;

public interface GameParametersJpaRepository
        extends ReactiveCrudRepository<GameParametersEntity, UUID> {

}
