package com.synthilearn.gameservice.domain;

import java.util.Map;
import java.util.UUID;

import com.synthilearn.gameservice.infra.rest.dto.PhraseInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameStatistic {
    private UUID id;
    private Integer translatesInGame;
    private Integer correctTranslates;
    private Integer incorrectTranslates;
    private Integer translatesLackTime;
    private Map<String, PhraseInfo> phraseInfos;
}
