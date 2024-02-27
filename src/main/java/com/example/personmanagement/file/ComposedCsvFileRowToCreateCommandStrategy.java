package com.example.personmanagement.file;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.person.model.CreatePersonCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class ComposedCsvFileRowToCreateCommandStrategy {

    private final Collection<CsvFileRowToCreateCommandStrategy> csvFileRowToCreateCommandStrategies;

    public CreatePersonCommand toCommand(String[] data) {
        CsvFileRowToCreateCommandStrategy strategy = csvFileRowToCreateCommandStrategies.stream().filter(s -> s.isSupported(data)).findFirst()
                .orElseThrow(() -> new InvalidStrategyTypeException("Invalid type!"));
        return strategy.toCommand(data);
    }
}