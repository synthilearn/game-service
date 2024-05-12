package com.synthilearn.gameservice.infra.rest.scheduler;

import java.time.ZonedDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.synthilearn.gameservice.domain.GameResult;
import com.synthilearn.gameservice.domain.GameStatus;
import com.synthilearn.gameservice.infra.persistence.jpa.entity.GameEntity;
import com.synthilearn.gameservice.infra.persistence.jpa.entity.GameStatisticEntity;
import com.synthilearn.gameservice.infra.persistence.jpa.entity.TranslateInGameEntity;
import com.synthilearn.gameservice.infra.persistence.jpa.repository.GameJpaRepository;
import com.synthilearn.gameservice.infra.persistence.jpa.repository.GameStatisticJpaRepository;
import com.synthilearn.gameservice.infra.persistence.jpa.repository.TranslateInGameJpaRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MainScheduler {

    private static final Logger log = LoggerFactory.getLogger(MainScheduler.class);
    private final GameJpaRepository gameJpaRepository;
    private final TranslateInGameJpaRepository translateInGameJpaRepository;
    private final GameStatisticJpaRepository gameStatisticJpaRepository;

    @Transactional
    @Scheduled(fixedRateString = "PT2S")
    public Mono<Void> finishGame() {
        log.info("Запуск джобы по смене статуса игры с IN_PROGRESS на GENERATE_STATISTIC");
        return gameJpaRepository.getInProgressGames()
                .flatMap(game -> translateInGameJpaRepository.findAllByGameId(game.getId())
                        .collectList()
                        .flatMap(translations -> {
                            if (gameIsFinished(game, translations)) {
                                game.setStatus(GameStatus.GENERATE_STATISTIC);
                                game.setResult(getGameResult(game, translations));
                                return gameJpaRepository.save(game);
                            }
                            return Mono.empty();
                        }))
                .then();
    }

    @Transactional
    @Scheduled(fixedRateString = "PT10S")
    public Mono<Void> startGame() {
        log.info("Запуск джобы по формированию статистики");
        return gameJpaRepository.getWaitingStatisticGenerateGames()
                .flatMap(game -> translateInGameJpaRepository.findAllByGameId(game.getId())
                        .collectList()
                        .flatMap(translations -> {
                            Integer translatesInGame =
                                    (int) translations.stream()
                                            .filter(TranslateInGameEntity::getCorrect)
                                            .count();
                            Integer correctTranslates = (int) translations.stream()
                                    .filter(x -> Boolean.TRUE.equals(x.getAnswer()))
                                    .filter(TranslateInGameEntity::getCorrect)
                                    .count();
                            Integer incorrectTranslates = (int) translations.stream()
                                    .filter(x -> Boolean.TRUE.equals(x.getAnswer()))
                                    .filter(translate -> !translate.getCorrect())
                                    .count();
                            gameStatisticJpaRepository.save(GameStatisticEntity.builder()
                                    .id(game.getId())
                                    .newRecord(true)
                                    .correctTranslates(correctTranslates)
                                    .incorrectTranslates(incorrectTranslates)
                                    .translatesInGame(translatesInGame)
                                    .translatesLackTime(translatesInGame - incorrectTranslates -
                                            correctTranslates)
                                    .build()).subscribe();
                            return gameJpaRepository.save(game.toBuilder()
                                    .status(GameStatus.FINISHED)
                                    .statisticCreated(true)
                                    .build());
                        }))
                .then();
    }

    private Boolean gameIsFinished(GameEntity game, List<TranslateInGameEntity> translates) {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime startTimeStage = game.getCreationDate();
        int allStages = game.getPhrasesInGame().size();
        int stage = 0;

        while (startTimeStage.isBefore(now) && ++stage <= allStages) {
            int finalStage = stage;
            startTimeStage = translates.stream()
                    .filter(translate ->
                            translate.getAnswerTime() != null &&
                                    translate.getQuestion() == finalStage)
                    .map(TranslateInGameEntity::getAnswerTime)
                    .findFirst()
                    .orElse(startTimeStage.plusSeconds(game.getTimeOnWord() + 2));
        }
        return stage > allStages;
    }

    private GameResult getGameResult(GameEntity game, List<TranslateInGameEntity> translates) {
        return ((double) game.getPhrasesInGame().size() -
                translates.stream().map(TranslateInGameEntity::getCorrect)
                        .filter(correct -> correct)
                        .count()) > 0.9 ? GameResult.SUCCESS : GameResult.FAILED;
    }
}
