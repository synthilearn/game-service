package com.synthilearn.gameservice.domain.mapper;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.synthilearn.gameservice.domain.Game;
import com.synthilearn.gameservice.domain.GameParameters;
import com.synthilearn.gameservice.domain.GameStatistic;
import com.synthilearn.gameservice.domain.PartOfSpeech;
import com.synthilearn.gameservice.domain.PhraseType;
import com.synthilearn.gameservice.domain.TranslateInGame;
import com.synthilearn.gameservice.infra.persistence.jpa.entity.GameEntity;
import com.synthilearn.gameservice.infra.persistence.jpa.entity.GameParametersEntity;
import com.synthilearn.gameservice.infra.persistence.jpa.entity.GameStatisticEntity;
import com.synthilearn.gameservice.infra.persistence.jpa.entity.TranslateInGameEntity;
import com.synthilearn.gameservice.infra.rest.dto.GameResponseDto;

@Mapper(componentModel = "spring")
public interface DomainMapper {

    Game map(GameEntity entity);
    GameResponseDto mapToDto(GameEntity entity);
    GameStatistic map(GameStatisticEntity entity);

    @Mapping(target = "partsOfSpeech", expression = "java(mapStringToPartsOfSpeechList(entity.getPartsOfSpeech()))")
    @Mapping(target = "phraseTypes", expression = "java(mapStringToTypeList(entity.getPhraseTypes()))")
    GameParameters map(GameParametersEntity entity);

    TranslateInGame map(TranslateInGameEntity entity);

    TranslateInGameEntity map(TranslateInGame domain);

    default List<PhraseType> mapStringToTypeList(String string) {
        if (string == null) {
            return new ArrayList<>();
        }
        String[] groupNames = string.substring(1, string.length() - 1).split(", ");
        List<PhraseType> groupsList = new ArrayList<>();
        for (String groupName : groupNames) {
            groupsList.add(PhraseType.valueOf(groupName.trim()));
        }
        return groupsList;
    }

    default List<PartOfSpeech> mapStringToPartsOfSpeechList(String string) {
        if (string == null) {
            return new ArrayList<>();
        }
        String[] groupNames = string.substring(1, string.length() - 1).split(", ");
        List<PartOfSpeech> groupsList = new ArrayList<>();
        for (String groupName : groupNames) {
            groupsList.add(PartOfSpeech.valueOf(groupName.trim()));
        }
        return groupsList;
    }
}
