package com.example.personmanagement.file.processor;

import com.example.personmanagement.exception.ResourceNotFoundException;
import com.example.personmanagement.file.ComposedCsvFileRowToCreateCommandStrategy;
import com.example.personmanagement.file.FileInformation;
import com.example.personmanagement.file.storage.FileStorage;
import com.example.personmanagement.person.model.CreatePersonCommand;
import com.example.personmanagement.person.model.PersonFileImportStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileProcessor {

    public record Result(long lastProcessedRow, boolean isFinished) {
    }

    private final Map<String, PersonFileImportStrategy> fileImportStrategyMap;

    private final ComposedCsvFileRowToCreateCommandStrategy csvFileRowToCreateCommandStrategy;

    private final FileStorage fileStorage;

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public Result processFile(FileInformation fileInformation, long batchStart, long batchSize) throws IOException {
        AtomicInteger processedLines = new AtomicInteger();

        try (BufferedReader reader = fileStorage.load(fileInformation.getFilePath())) {
            var lines = reader.lines();
            Stream<String> batchLines = lines.skip(1).skip(batchStart).limit(batchSize);
            batchLines.forEach(line -> {
                processFileLine(line);
                processedLines.getAndIncrement();
            });
        }

        boolean isFinished = processedLines.get() < batchSize;
        return new Result(batchStart + processedLines.get(), isFinished);
    }

    private void processFileLine(String line) {
        String[] data = line.split(",");
        String type = data[0];
        String key = type.toLowerCase() + "FileImportStrategy";
        PersonFileImportStrategy strategy = fileImportStrategyMap.get(key);

        if (strategy != null) {
            CreatePersonCommand command = mapDataToCommand(data);
            strategy.insert(command, jdbcTemplate);
        } else {
            throw new ResourceNotFoundException("Unknown type: " + type);
        }
    }

    private CreatePersonCommand mapDataToCommand(String[] data) {
        return csvFileRowToCreateCommandStrategy.toCommand(data);
    }
}