package com.example.personmanagement.exception;

import jakarta.persistence.RollbackException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
@Slf4j
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
        log.error("Resource Not Found exception",exception);
        return createExceptionDto("Resource not found!");
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
        log.error("Data integrity violation",exception);
        return createExceptionDto("Data integrity violation!");
    }

    @ExceptionHandler(DuplicateResourceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionDto handleDuplicateResourceException(DuplicateResourceException exception) {
        log.error("Duplicated Resource exception",exception);
        return createExceptionDto("Duplicated Resource!");
    }

    @ExceptionHandler(InvalidStrategyTypeException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ExceptionDto handleInvalidStrategyTypeException(InvalidStrategyTypeException exception) {
        log.error("Strategy Type exception",exception);
        return createExceptionDto("Not found strategy type!");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleConstraintViolationException(ConstraintViolationException exception) {
        log.error("Constraint Violation", exception);
        return createExceptionDto("Constratint exception!");
    }

    @ExceptionHandler(RollbackException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleRollbackException(RollbackException exception) {
        log.error("Rollback", exception);
        return createExceptionDto("Rollback exception!");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ExceptionDto handleInvalidStrategyTypeException(IllegalArgumentException exception) {
        log.error("Illegal argument!",exception);
        return createExceptionDto("Illegal argument!");
    }

    @ExceptionHandler(JobOverlappingException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ExceptionDto handleJobOverlappingException(JobOverlappingException exception) {
        log.error("New job position overlaps with existing position!",exception);
        return createExceptionDto("New job position overlaps with existing position");
    }

    private ExceptionDto createExceptionDto(String message) {
        return new ExceptionDto(message);
    }
}