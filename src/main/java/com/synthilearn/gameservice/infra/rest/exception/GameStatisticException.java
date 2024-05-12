package com.synthilearn.gameservice.infra.rest.exception;

import java.util.UUID;

import org.springframework.http.HttpStatus;

public class GameStatisticException extends GenericException {
    public GameStatisticException(String message, HttpStatus status, Integer code) {
        super(message, status, code);
    }

    public static GameStatisticException notFound(UUID gameId) {
        return new GameStatisticException(
                String.format("Game parameters not found for game: %s", gameId),
                HttpStatus.BAD_REQUEST, 1000);
    }
}
