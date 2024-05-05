package com.synthilearn.gameservice.domain;

import java.time.ZonedDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"translationText", "partOfSpeech"})
public class PhraseTranslate {

    private UUID id;
    private UUID phraseId;
    private String translationText;
    private String status;
    private PartOfSpeech partOfSpeech;
    private ZonedDateTime creationDate;
    private Integer learnLevel;
}
