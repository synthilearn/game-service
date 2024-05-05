package com.synthilearn.gameservice.infra.persistence.jpa.repository;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.synthilearn.gameservice.infra.persistence.jpa.entity.GameEntity;

import reactor.core.publisher.Mono;

@Repository
public interface GameJpaRepository extends ReactiveCrudRepository<GameEntity, UUID> {

    @Query("""
            SELECT g.* FROM game g
            WHERE g.workarea_id = :workareaId
            ORDER BY g.creation_date DESC
            LIMIT 1
    """)
    Mono<GameEntity> findTopByOrderByCreationDateDesc(UUID workareaId);

    @Query("""
            SELECT g.* FROM game g
            WHERE g.workarea_id = :workareaId
            AND g.status = 'IN_PROGRESS'
            ORDER BY g.creation_date DESC
            LIMIT 1
    """)
    Mono<GameEntity> getCurrentGame(UUID workareaId);
}