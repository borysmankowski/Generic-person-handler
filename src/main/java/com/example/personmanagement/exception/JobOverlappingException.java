package com.example.personmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)

public class JobOverlappingException extends RuntimeException {
    public JobOverlappingException(String message) {
        super(message);
    }
}
