package com.synthilearn.gameservice.infra.rest.exception;

import java.util.UUID;

import org.springframework.http.HttpStatus;

public class StageException extends GenericException {

    private StageException(String message, HttpStatus status, Integer code) {
        super(message, status, code);
    }

    public static StageException missMatch(Integer currentStage, Integer requestedStage,
                                           UUID gameId) {
        return new StageException(
                String.format(
                        "Miss match for requested stage: %s, while current stage: %s, for game: %s",
                        requestedStage, currentStage, gameId),
                HttpStatus.BAD_REQUEST, 1010);
    }
}
