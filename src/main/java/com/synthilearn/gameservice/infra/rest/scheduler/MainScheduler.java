package com.synthilearn.gameservice.infra.rest.scheduler;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synthilearn.gameservice.domain.GameResult;
import com.synthilearn.gameservice.domain.GameStatus;
import com.synthilearn.gameservice.infra.adapter.DictionaryClient;
import com.synthilearn.gameservice.infra.persistence.jpa.entity.GameEntity;
import com.synthilearn.gameservice.infra.persistence.jpa.entity.GameStatisticEntity;
import com.synthilearn.gameservice.infra.persistence.jpa.entity.TranslateInGameEntity;
import com.synthilearn.gameservice.infra.persistence.jpa.repository.GameJpaRepository;
import com.synthilearn.gameservice.infra.persistence.jpa.repository.GameStatisticJpaRepository;
import com.synthilearn.gameservice.infra.persistence.jpa.repository.TranslateInGameJpaRepository;
import com.synthilearn.gameservice.infra.rest.dto.ChangeProgressRequest;
import com.synthilearn.gameservice.infra.rest.dto.PhraseInfo;
import com.synthilearn.gameservice.infra.rest.dto.ProgressEntry;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MainScheduler {

    private static final Logger log = LoggerFactory.getLogger(MainScheduler.class);
    private final GameJpaRepository gameJpaRepository;
    private final TranslateInGameJpaRepository translateInGameJpaRepository;
    private final GameStatisticJpaRepository gameStatisticJpaRepository;
    private final DictionaryClient dictionaryClient;

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
    public Mono<Void> generateStatistics() {
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

                            Map<String, PhraseInfo> stringPhraseInfoMap =
                                    generatePhrases(translations, game);

                            ChangeProgressRequest request = new ChangeProgressRequest();
                            stringPhraseInfoMap.forEach((key, value) -> request.getProgressEntries()
                                    .add(new ProgressEntry(value.getCorrectAnswerId(),
                                            value.getNewProgress())));

                            try {
                                gameStatisticJpaRepository.save(GameStatisticEntity.builder()
                                        .id(game.getId())
                                        .newRecord(true)
                                        .answerInfo(new ObjectMapper().writeValueAsString(
                                                stringPhraseInfoMap))
                                        .correctTranslates(correctTranslates)
                                        .incorrectTranslates(incorrectTranslates)
                                        .translatesInGame(translatesInGame)
                                        .translatesLackTime(translatesInGame - incorrectTranslates -
                                                correctTranslates)
                                        .build()).subscribe();
                            } catch (JsonProcessingException e) {
                                return Mono.error(new RuntimeException(e));
                            }
                            gameJpaRepository.save(game.toBuilder()
                                    .status(GameStatus.FINISHED)
                                    .statisticCreated(true)
                                    .build()).subscribe();

                            if (!request.getProgressEntries().isEmpty()) {
                                return dictionaryClient.changeProgress(request);
                            }
                            return Mono.empty();
                        }))
                .then();
    }

    private Map<String, PhraseInfo> generatePhrases(List<TranslateInGameEntity> translations,
                                                    GameEntity game) {
        Map<Integer, List<TranslateInGameEntity>> map = translations.stream()
                .collect(Collectors.groupingBy(TranslateInGameEntity::getQuestion, TreeMap::new,
                        Collectors.toList()));

        List<String> phrases = game.getPhrasesInGame()
                .stream()
                .map(x -> x.replaceAll("[\"\\\\{}]", ""))
                .toList();

        Map<String, PhraseInfo> phrasesInfoMap = new HashMap<>();

        for (Map.Entry<Integer, List<TranslateInGameEntity>> entry : map.entrySet()) {
            Optional<TranslateInGameEntity> answeredTranslate =
                    entry.getValue().stream().filter(x -> Boolean.TRUE.equals(x.getAnswer()))
                            .findFirst();
            TranslateInGameEntity trueAnswer =
                    entry.getValue().stream().filter(TranslateInGameEntity::getCorrect).findFirst()
                            .orElseThrow();
            String phrase = phrases.get(entry.getKey() - 1);

            answeredTranslate.ifPresent(translate -> phrasesInfoMap.put(phrase,
                    new PhraseInfo(translate.getTranslateText(), translate.getCorrect(),
                            trueAnswer.getTranslateId(), trueAnswer.getTranslateText(),
                            translate.getOldProgress(),
                            calculateProgress(translate, trueAnswer))));
        }

        return phrasesInfoMap;
    }

    private Integer calculateProgress(TranslateInGameEntity entity,
                                      TranslateInGameEntity trueAnswer) {
        return Objects.equals(trueAnswer.getTranslateId(), entity.getTranslateId()) ?
                Math.min(trueAnswer.getOldProgress() + 3, 100) :
                Math.max(trueAnswer.getOldProgress() - 3, 0);
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
        log.info("***** {}", translates.stream()
                .filter(x -> Boolean.TRUE.equals(x.getAnswer()))
                .filter(TranslateInGameEntity::getCorrect)
                .count());
        return ((double) translates.stream()
                .filter(x -> Boolean.TRUE.equals(x.getAnswer()))
                .filter(TranslateInGameEntity::getCorrect)
                .count() / game.getPhrasesInGame().size()) >= 0.8 ? GameResult.SUCCESS :
                GameResult.FAILED;
    }
}
