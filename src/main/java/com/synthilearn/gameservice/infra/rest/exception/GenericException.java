package com.synthilearn.gameservice.infra.rest.exception;

import org.springframework.http.HttpStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GenericException extends RuntimeException {

    private final HttpStatus status;
    private final Integer code;

    public GenericException(String message, HttpStatus status, Integer code) {
        super(message);
        this.status = status;
        this.code = code;
    }
}