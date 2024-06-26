package com.synthilearn.gameservice.infra.persistence.jpa.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.synthilearn.gameservice.infra.persistence.jpa.entity.TranslateInGameEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface TranslateInGameJpaRepository extends
        ReactiveCrudRepository<TranslateInGameEntity, UUID> {

    Flux<TranslateInGameEntity> findAllByGameId(UUID gameId);

    Mono<TranslateInGameEntity> findByTranslateTextAndGameIdAndQuestion(String translateText,
                                                                        UUID gameId,
                                                                        Integer question);

    Mono<TranslateInGameEntity> findByGameIdAndQuestionAndCorrect(UUID gameId, Integer stage,
                                                                    Boolean correct);
}
