package com.example.personmanagement.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHanlder {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorDto handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        ValidationErrorDto errorDto = new ValidationErrorDto();
        exception.getFieldErrors().forEach(error ->
                errorDto.addViolation(error.getField(), error.getDefaultMessage()));
        return errorDto;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDto handleResourceNotFoundException(ResourceNotFoundException exception) {
        return createExceptionDto(exception.getMessage());
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
        return createExceptionDto(exception.getMessage());
    }

    @ExceptionHandler(DuplicateResourceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionDto handleDuplicateResourceException(DuplicateResourceException exception) {
        return createExceptionDto(exception.getMessage());
    }

    @ExceptionHandler(InvalidStrategyTypeException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ExceptionDto handleInvalidStrategyTypeException(InvalidStrategyTypeException exception) {
        return createExceptionDto(exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ExceptionDto handleInvalidStrategyTypeException(IllegalArgumentException exception) {
        return createExceptionDto(exception.getMessage());
    }

    private ExceptionDto createExceptionDto(String message) {
        return new ExceptionDto(message);
    }
}