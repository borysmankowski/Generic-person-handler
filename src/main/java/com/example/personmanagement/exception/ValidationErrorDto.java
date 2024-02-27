package com.example.personmanagement.exception;

import lombok.Getter;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ValidationErrorDto extends ExceptionDto {

    private final List<ValidationErrorInfo> violations = new ArrayList<>();

    public ValidationErrorDto() {
        super("validation errors");
    }

    public void addViolation(String field, String message) {
        violations.add(new ValidationErrorInfo(field, message));
    }

    @Value
    public static class ValidationErrorInfo {
        String field;
        String message;
    }
}
