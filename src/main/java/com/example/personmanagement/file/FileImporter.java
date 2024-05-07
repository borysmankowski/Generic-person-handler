package com.example.personmanagement.file;

import com.example.personmanagement.exception.ResourceNotFoundException;
import com.example.personmanagement.person.PersonCreationStrategy;
import com.example.personmanagement.person.PersonRepository;
import com.example.personmanagement.person.model.CreatePersonCommand;
import com.example.personmanagement.person.model.Person;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileImporter {

    public record Result(long lastProcessedRow, boolean isFinished) {
    }

    private final PersonRepository personRepository;

    private final Map<String, PersonCreationStrategy> creationStrategyMap;

    private final ComposedCsvFileRowToCreateCommandStrategy csvFileRowToCreateCommandStrategy;

    private final FileStorage fileStorage;

    @Transactional
    public Result processFile(FileImport fileImport, long batchStart, long batchSize) throws IOException {
        AtomicInteger processedLines = new AtomicInteger();
        try (BufferedReader reader = fileStorage.load(fileImport.getFilePath())) {
            var lines = reader.lines();
            Stream<String> batchLines = lines.skip(1).skip(batchStart).limit(batchSize);
            List<Person> entities = batchLines.map(line -> {
                Person person = processFileLine(line);
                processedLines.getAndIncrement();
                return person;
            }).collect(Collectors.toList());
            personRepository.saveAllAndFlush(entities);
        }

        boolean isFinished = processedLines.get() < batchSize;
        return new Result(batchStart + processedLines.get(), isFinished);
    }

    private Person processFileLine(String line) {
        String[] data = line.split(",");
        String type = data[0];
        PersonCreationStrategy strategy = creationStrategyMap.get(type);
        Person person;

        if (strategy != null) {
            person = createAndAddToDatabase(strategy, data);
        } else {
            throw new ResourceNotFoundException("Unknown type: " + type);
        }
        return person;
    }

    private Person createAndAddToDatabase(PersonCreationStrategy strategy, String[] data) {
        CreatePersonCommand command = mapDataToCommand(data);
        return strategy.create(command);
    }

    private CreatePersonCommand mapDataToCommand(String[] data) {
        return csvFileRowToCreateCommandStrategy.toCommand(data);
    }
}