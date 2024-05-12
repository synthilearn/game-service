package com.synthilearn.gameservice.infra.rest.dto;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CurrentGameResponseDto {

    private String currentPhrase;
    private Integer currentStage;
    private Integer allStages;
    private ZonedDateTime stageEndTime;
    private ZonedDateTime gameStartedTime;
    private List<String> answerOptions;
    private GameStateDto state;
    private UUID gameId;


    public CurrentGameResponseDto(GameStateDto state) {
        this.state = state;
    }

    public CurrentGameResponseDto(ZonedDateTime gameStartedTime, GameStateDto state) {
        this.gameStartedTime = gameStartedTime;
        this.state = state;
    }
}
