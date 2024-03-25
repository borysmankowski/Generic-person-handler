
package com.example.personmanagement.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
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
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileImporter {

    private final PersonRepository personRepository;

    private final Map<String, PersonCreationStrategy> creationStrategyMap;

    private final ComposedCsvFileRowToCreateCommandStrategy csvFileRowToCreateCommandStrategy;

    private final AmazonS3 amazonS3;

    @Transactional
    public void processFileWithLoad(FileImport fileImport, Consumer<Integer> onRowProcessed) {
        String filePath = fileImport.getFilePath();

        S3Object getObjectResult = amazonS3.getObject("person-management-bucket", filePath);
        S3ObjectInputStream fileInputStream = getObjectResult.getObjectContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
        String currentLine = null;

        try (var lines = reader.lines()) {
            Stream<String> batchLines = lines.skip(1).skip(fileImport.getLastProcessedRow());
            Iterator<String> iterator = batchLines.iterator();
            int processedLines = 0;
            while (iterator.hasNext()) {
                currentLine = iterator.next();
                processFileLine(currentLine);
                processedLines++;
                onRowProcessed.accept(processedLines);
            }
        }
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


