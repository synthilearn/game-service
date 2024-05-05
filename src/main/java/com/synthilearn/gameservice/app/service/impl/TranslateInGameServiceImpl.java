package com.synthilearn.gameservice.app.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.synthilearn.gameservice.app.service.TranslateInGameService;
import com.synthilearn.gameservice.domain.Phrase;
import com.synthilearn.gameservice.domain.PhraseTranslate;
import com.synthilearn.gameservice.infra.adapter.DictionaryClient;
import com.synthilearn.gameservice.infra.adapter.dto.AllPhraseRequestDto;
import com.synthilearn.gameservice.infra.persistence.jpa.entity.TranslateInGameEntity;
import com.synthilearn.gameservice.infra.persistence.jpa.repository.TranslateInGameJpaRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TranslateInGameServiceImpl implements TranslateInGameService {

    private final DictionaryClient dictionaryClient;
    private final TranslateInGameJpaRepository repository;

    @Transactional
    public Mono<List<String>> defineTranslatesInGame(AllPhraseRequestDto allPhraseRequestDto,
                                                     UUID gameId) {
        return dictionaryClient.getPhrases(allPhraseRequestDto)
                .flatMap(phrases -> {
                    List<Phrase> phraseCopy = new ArrayList<>(phrases);
                    List<String> phrasesInGame = new ArrayList<>();
                    Random random = new Random();
                    List<PhraseTranslate> remainingTranslates = new ArrayList<>(phraseCopy.stream()
                            .flatMap(phrase -> phrase.getPhraseTranslates().stream())
                            .toList());

                    for (int i = 1; i <= allPhraseRequestDto.getTranslatesAmount(); i++) {
                        Phrase randomPhrase = phraseCopy.get(random.nextInt(phraseCopy.size()));
                        phrasesInGame.add(randomPhrase.getText());
                        phraseCopy.remove(randomPhrase);
                        remainingTranslates.removeAll(randomPhrase.getPhraseTranslates());

                        PhraseTranslate randomPhraseTranslate = randomPhrase.getPhraseTranslates()
                                .get(random.nextInt(randomPhrase.getPhraseTranslates().size()));
                        repository.save(initTranslate(randomPhraseTranslate, i, true, gameId))
                                .subscribe();

                        int finalI = i;
                        remainingTranslates.stream()
                                .skip(Math.min(remainingTranslates.size() - 5,
                                        random.nextInt(remainingTranslates.size())))
                                .limit(5)
                                .toList()
                                .forEach(fakeTranslate -> repository
                                        .save(initTranslate(fakeTranslate, finalI, false, gameId))
                                        .subscribe());
                    }

                    return Mono.just(phrasesInGame);
                });
    }

    private TranslateInGameEntity initTranslate(PhraseTranslate phraseTranslate, int iteration,
                                                Boolean correct, UUID gameId) {
        return TranslateInGameEntity.builder()
                .id(UUID.randomUUID())
                .newRecord(true)
                .oldProgress(phraseTranslate.getLearnLevel())
                .translateId(phraseTranslate.getId())
                .gameId(gameId)
                .transaltePartOfSpeech(phraseTranslate.getPartOfSpeech())
                .translateText(phraseTranslate.getTranslationText())
                .question(iteration)
                .correct(correct)
                .build();
    }

}
