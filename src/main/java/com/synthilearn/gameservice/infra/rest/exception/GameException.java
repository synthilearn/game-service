package com.synthilearn.gameservice.infra.rest.exception;

import java.util.List;
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

    public static GameException inProgress(UUID workareaId) {
        return new GameException(
                String.format("Game in progress for workarea: %s", workareaId),
                HttpStatus.BAD_REQUEST, 1005);
    }

    public static GameException translateNotFound(String requestedTranslate,
                                                  List<String> currentTranslates, UUID gameId) {
        return new GameException(
                String.format(
                        "Requested translate: %s not found in current translates: %s for gameId: %s",
                        requestedTranslate, currentTranslates, gameId),
                HttpStatus.NOT_FOUND, 1015);
    }
}