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
public class GlobalExceptionHandler {

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

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionDto> handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
        log.error("Data integrity violation", exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionDto("Data integrity violation!"));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionDto> handleConstraintViolationException(ConstraintViolationException exception) {
        log.error("Constraint Violation", exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionDto("Constraint violation!"));
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ExceptionDto> handleIOException(IOException exception) {
        log.error("IOException", exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionDto("IOException!"));
    }

    @ExceptionHandler(RollbackException.class)
    public ResponseEntity<ExceptionDto> handleRollbackException(RollbackException exception) {
        log.error("Rollback", exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionDto("Rollback exception!"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionDto> handleIllegalArgumentException(IllegalArgumentException exception) {
        log.error("Illegal argument!", exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionDto("Illegal argument!"));
    }

    @ExceptionHandler(JobOverlappingException.class)
    public ResponseEntity<ExceptionDto> handleJobOverlappingException(JobOverlappingException exception) {
        log.error("New job position overlaps with existing position", exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionDto("New job position overlaps with existing position"));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ExceptionDto> handleDuplicateResourceException(DuplicateResourceException exception) {
        log.error("Duplicated Resource exception", exception);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ExceptionDto("Duplicated Resource!"));
    }

    @ExceptionHandler(ResourceVersionNotValidException.class)
    public ResponseEntity<ExceptionDto> handleResourceVersionNotValidException(ResourceVersionNotValidException exception) {
        log.error("Resource Version Not Valid exception", exception);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ExceptionDto("Person was modified during your update, please fetch the newest version and retry"));
    }

    @ExceptionHandler(InvalidStrategyTypeException.class)
    public ResponseEntity<ExceptionDto> handleInvalidStrategyTypeException(InvalidStrategyTypeException exception) {
        log.error("Strategy Type exception", exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionDto("Not found strategy type!"));
    }

    @ExceptionHandler(SalaryNotValidException.class)
    public ResponseEntity<ExceptionDto> handleSalaryNotValidException(SalaryNotValidException exception) {
        log.error("Salary Not Valid exception", exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionDto(exception.getMessage()));
    }

    @ExceptionHandler(FileImportException.class)
    public ResponseEntity<ExceptionDto> handleFileImportException(FileImportException exception) {
        log.error("File Import exception", exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionDto(exception.getMessage()));
    }
}
