package com.example.personmanagement.file;

import com.example.personmanagement.person.model.CreatePersonCommand;

public interface CsvFileRowToCreateCommandStrategy {
    CreatePersonCommand toCommand(String[] data);

    boolean isSupported(String[] data);
}