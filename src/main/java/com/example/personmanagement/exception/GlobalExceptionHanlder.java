package com.example.personmanagement.exception;

import jakarta.persistence.RollbackException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.io.IOException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHanlder {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        ValidationErrorDto errorDto = new ValidationErrorDto();
        exception.getFieldErrors().forEach(error ->
                errorDto.addViolation(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionDto> handleResourceNotFoundException(ResourceNotFoundException exception) {
        log.error("Resource Not Found exception", exception);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionDto("Resource not found!"));
    }

    @ExceptionHandler({
            DataIntegrityViolationException.class,
            ConstraintViolationException.class,
            IOException.class,
            RollbackException.class,
            IllegalArgumentException.class,
            JobOverlappingException.class
    })
    public ResponseEntity<ExceptionDto> handleBadRequestExceptions(Exception exception) {
        String message;
        if (exception instanceof DataIntegrityViolationException) {
            log.error("Data integrity violation", exception);
            message = "Data integrity violation!";
        } else if (exception instanceof ConstraintViolationException) {
            log.error("Constraint Violation", exception);
            message = "Constraint violation!";
        } else if (exception instanceof IOException) {
            log.error("IOException", exception);
            message = "IOException!";
        } else if (exception instanceof RollbackException) {
            log.error("Rollback", exception);
            message = "Rollback exception!";
        } else if (exception instanceof IllegalArgumentException) {
            log.error("Illegal argument!", exception);
            message = "Illegal argument!";
        } else if (exception instanceof JobOverlappingException) {
            log.error("New job position overlaps with existing position", exception);
            message = "New job position overlaps with existing position";
        } else {
            log.error("Unhandled exception", exception);
            message = "Bad request!";
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionDto(message));
    }

    @ExceptionHandler({DuplicateResourceException.class, ResourceVersionNotValidException.class})
    public ResponseEntity<ExceptionDto> handleConflictExceptions(RuntimeException exception) {
        if (exception instanceof DuplicateResourceException) {
            log.error("Duplicated Resource exception", exception);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ExceptionDto("Duplicated Resource!"));
        } else if (exception instanceof ResourceVersionNotValidException) {
            log.error("Resource Version Not Valid exception", exception);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ExceptionDto("Person was modified during your update, please fetch the newest version and retry"));
        } else {
            log.error("Unhandled exception", exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ExceptionDto("An unexpected error occurred"));
        }
    }

    @ExceptionHandler(InvalidStrategyTypeException.class)
    public ResponseEntity<ExceptionDto> handleInvalidStrategyTypeException(InvalidStrategyTypeException exception) {
        log.error("Strategy Type exception", exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionDto("Not found strategy type!"));
    }
}