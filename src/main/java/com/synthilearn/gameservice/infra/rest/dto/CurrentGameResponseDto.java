package com.synthilearn.gameservice.infra.rest.dto;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

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
    private Boolean isStarted;

    public CurrentGameResponseDto(Boolean isFinished) {
        this.isFinished = isFinished;
    }

    private Boolean isFinished;

    public CurrentGameResponseDto(ZonedDateTime gameStartedTime, Boolean isStarted) {
        this.gameStartedTime = gameStartedTime;
        this.isStarted = isStarted;
    }
}
