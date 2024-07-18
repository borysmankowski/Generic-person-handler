package com.example.personmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)

public class InvalidStrategyTypeException extends RuntimeException {

    public InvalidStrategyTypeException(String message) {
        super(message);
    }
}
