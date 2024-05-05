package com.synthilearn.gameservice.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GameParameters {
    private Integer translatesAmount;
    private List<PartOfSpeech> availablePartOfSpeech;
    private List<PhraseType> phraseTypes;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private Integer timeOnWord;
    private TypeOfGame typeOfGame;
}
