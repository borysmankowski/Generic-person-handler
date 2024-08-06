package com.example.personmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)

public class FileImportException extends RuntimeException {
    public FileImportException(String message) {
        super(message);
    }
}
