package com.synthilearn.gameservice.domain;


import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Game {

    private UUID id;
    private LocalDateTime creationDate;
    private Boolean statisticCreated;
    private GameResult result;
    private GameStatus status;
}
