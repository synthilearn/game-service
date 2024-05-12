package com.synthilearn.gameservice.domain;

import java.time.ZonedDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TranslateInGame {

    private UUID id;
    private UUID gameId;
    private UUID translateId;
    private String translateText;
    private PartOfSpeech partOfSpeech;
    private Integer oldProgress;
    private Integer newProgress;
    private Integer question;
    private Boolean correct;
    private Boolean answer;
    private ZonedDateTime answerTime;
}
