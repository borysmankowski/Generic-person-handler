package com.example.personmanagement.file;

import com.example.personmanagement.person.model.CreatePersonCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;

public interface CsvFileRowToCreateCommandStrategy {
    CreatePersonCommand toCommand(String[] data);

    boolean isSupported(String[] data);
}