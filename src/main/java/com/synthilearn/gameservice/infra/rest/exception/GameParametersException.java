package com.synthilearn.gameservice.infra.rest.exception;

import java.util.UUID;

import org.springframework.http.HttpStatus;

public class GameParametersException extends GenericException {
    public GameParametersException(String message, HttpStatus status, Integer code) {
        super(message, status, code);
    }

    public static GameParametersException notFound(UUID workareaId) {
        return new GameParametersException(
                String.format("Game parameters not found for workarea: %s", workareaId),
                HttpStatus.OK, 1000);
    }
}