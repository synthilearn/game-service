package com.synthilearn.gameservice.app.service;

import java.util.List;
import java.util.UUID;

import com.synthilearn.gameservice.infra.adapter.dto.AllPhraseRequestDto;

import reactor.core.publisher.Mono;

public interface TranslateInGameService {

    Mono<List<String>> defineTranslatesInGame(AllPhraseRequestDto allPhraseRequestDto, UUID gameId);
}
