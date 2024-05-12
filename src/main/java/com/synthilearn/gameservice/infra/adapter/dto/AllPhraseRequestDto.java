package com.synthilearn.gameservice.infra.adapter.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.synthilearn.gameservice.domain.PartOfSpeech;
import com.synthilearn.gameservice.domain.PhraseType;
import com.synthilearn.gameservice.domain.TypeOfGame;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AllPhraseRequestDto {

    private final Integer page = 0;
    private final Integer size = Integer.MAX_VALUE;
    private final List<?> groups = new ArrayList<>();
    private final Boolean showTranslation = true;
    private final LocalDate dateFrom = LocalDate.ofYearDay(1900, 1);
    private final LocalDate dateTo = LocalDate.ofYearDay(2100, 1);

    private UUID dictionaryId;
    private List<PhraseType> phraseTypes;
    private List<PartOfSpeech> partsOfSpeech;
    private Integer translatesAmount;
    private TypeOfGame typeOfGame;
    private Integer timeOnWord;
}
