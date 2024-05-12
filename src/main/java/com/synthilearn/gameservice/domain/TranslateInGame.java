package com.synthilearn.gameservice.domain;

import java.time.ZonedDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

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
    private PartOfSpeech transaltePartOfSpeech;
    private Integer oldProgress;
    private Integer newProgress;
    private Integer question;
    private Boolean correct;
    private Boolean answer;
    private ZonedDateTime answerTime;
}
