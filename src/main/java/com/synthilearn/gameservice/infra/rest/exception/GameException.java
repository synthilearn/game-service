package com.synthilearn.gameservice.infra.rest.exception;

import java.util.UUID;

import org.springframework.http.HttpStatus;

public class GameException extends GenericException {
    public GameException(String message, HttpStatus status, Integer code) {
        super(message, status, code);
    }

    public static GameException alreadyExists(UUID workareaId) {
        return new GameException(
                String.format("Game already in process for workarea: %s", workareaId),
                HttpStatus.BAD_REQUEST, 1000);
    }

    public static GameException notFound(UUID workareaId) {
        return new GameException(
                String.format("Game not found for workarea: %s", workareaId),
                HttpStatus.BAD_REQUEST, 1000);
    }

    public static GameException finished(UUID workareaId) {
        return new GameException(
                String.format("Game already finished for workarea: %s", workareaId),
                HttpStatus.BAD_REQUEST, 1005);
    }
}