package com.synthilearn.gameservice.infra.rest.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.synthilearn.gameservice.domain.GameResult;
import com.synthilearn.gameservice.domain.GameStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameResponseDto {
    private UUID id;
    private LocalDateTime creationDate;
    private Boolean statisticCreated;
    private GameResult result;
    private GameStatus status;
    private List<String> phrasesInGame;
}
