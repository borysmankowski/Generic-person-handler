
package com.example.personmanagement.file;

import com.example.personmanagement.exception.ResourceNotFoundException;
import com.example.personmanagement.person.PersonCreationStrategy;
import com.example.personmanagement.person.PersonRepository;
import com.example.personmanagement.person.model.CreatePersonCommand;
import com.example.personmanagement.person.model.Person;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
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

    @Value("${spring.upload.dir}")
    private String UPLOAD_DIR;

    @Transactional
    public Result processFile(FileImport fileImport, long batchStart, long batchSize) throws FileNotFoundException {

        Path filePath = Path.of(UPLOAD_DIR,fileImport.getFilePath());

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(String.valueOf(filePath))));

        String currentLine = null;

        int processedLines = 0;
        try (var lines = reader.lines()) {
            Stream<String> batchLines = lines.skip(1).skip(batchStart).limit(batchSize);
            Iterator<String> iterator = batchLines.iterator();
            while (iterator.hasNext()) {
                currentLine = iterator.next();
                processFileLine(currentLine);
                processedLines++;
            }
        }

        var isFinished = processedLines < batchSize;
        return new Result(batchStart + processedLines, isFinished);
    }

    private void processFileLine(String line) {
        String[] data = line.split(",");
        String type = data[0];
        PersonCreationStrategy strategy = creationStrategyMap.get(type);

        if (strategy != null) {
            createAndAddToDatabase(strategy, data);
        } else {
            throw new ResourceNotFoundException("Unknown type: " + type);
        }
    }

    private void createAndAddToDatabase(PersonCreationStrategy strategy, String[] data) {
        CreatePersonCommand command = mapDataToCommand(data);
        Person person = strategy.create(command);
        personRepository.save(person);
    }

    private CreatePersonCommand mapDataToCommand(String[] data) {
        return csvFileRowToCreateCommandStrategy.toCommand(data);
    }
}
