package com.example.personmanagement.file;

import com.example.personmanagement.exception.DuplicateResourceException;
import com.example.personmanagement.exception.ResourceNotFoundException;
import com.example.personmanagement.person.PersonCreationStrategy;
import com.example.personmanagement.person.PersonRepository;
import com.example.personmanagement.person.model.CreatePersonCommand;
import com.example.personmanagement.person.model.Person;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final PersonRepository personRepository;

    private final Map<String, PersonCreationStrategy> creationStrategyMap;

    private final FileImportRepository fileImportRepository;

    private final ComposedCsvFileRowToCreateCommandStrategy csvFileRowToCreateCommandStrategy;

    private final PeselValidator peselValidator;

    @Value("${spring.upload.dir}")
    private String UPLOAD_DIR;

    public ResponseEntity<String> uploadFile(InputStream inputsStream, String originalFilename) {
        try {
            String uniqueFilename = System.currentTimeMillis() + "_" + originalFilename;
            Path filePath = Path.of(UPLOAD_DIR, uniqueFilename);
            Files.copy(inputsStream, filePath, StandardCopyOption.REPLACE_EXISTING);

            FileImport fileImport = FileImport.builder()
                    .filePath(filePath.toString())
                    .lastProcessedRow(0L)
                    .status(FileStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .build();

            fileImportRepository.save(fileImport);

            return ResponseEntity.ok("File uploaded successfully. FilePath: " + filePath);

        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to upload the file.");
        }
    }

    @Async
    public CompletableFuture<Optional<Long>> findFileToProcess() {
        return CompletableFuture.completedFuture(fileImportRepository
                .findFirstByStatusOrderByCreatedAtAsc(FileStatus.PENDING)
                .map(FileImport::getId));
    }

    @Transactional
    public void processFile(Long fileImportId) {
        FileImport fileImport = fileImportRepository.findById(fileImportId)
                .orElseThrow(() -> new ResourceNotFoundException("Import file with id: " + fileImportId + " hasnt been found"));
        String filePath = fileImport.getFilePath();

        if (fileImport.getStartedAt() == null) {
            fileImport.setStartedAt(LocalDateTime.now());
        }

        try (var lines = Files.lines(Path.of(filePath))) {
            var batchLines = lines.skip(1).skip(fileImport.getLastProcessedRow());
            var iterator = batchLines.iterator();
            var processedLines = 0;
            try {
                while (iterator.hasNext()) {
                    processFileLine(iterator.next());
                    processedLines++;
                }
                fileImport.setStatus(FileStatus.SUCCESS);
            } catch (Exception e) {
                fileImport.setStatus(FileStatus.FAILED);
            } finally {
                fileImport.setLastProcessedRow(fileImport.getLastProcessedRow() + processedLines);
                fileImport.setFinishedAt(LocalDateTime.now());
                fileImportRepository.save(fileImport);
            }
        } catch (IOException e) {
            fileImport.setStatus(FileStatus.FAILED);
            log.error("Error when processing file");
        }
    }

    private void processFileLine(String line) {
        String[] data = line.split(",");
        String type = data[0];
        PersonCreationStrategy strategy = creationStrategyMap.get(type);

        if (strategy != null) {
            CreatePersonCommand command = mapDataToCommand(data);

            try {
                peselValidator.validate(command.getPesel());
                createAndAddToDatabase(strategy, data);
            } catch (DuplicateResourceException e) {
                log.warn("Skipping line due to duplicate Pesel: {}", line);
            }
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

    public ResponseEntity<Map<String, Object>> getFileImportStatus(Long id) {
        Optional<FileImport> fileImportOptional = fileImportRepository.findById(id);
        return fileImportOptional.map(this::buildStatusResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private Map<String, Object> buildStatusResponse(FileImport fileImport) {
        var result = new HashMap<String, Object>();
        result.put("status", fileImport.getStatus());
        result.put("createdDate", fileImport.getCreatedAt());
        result.put("startDate", fileImport.getStartedAt());
        result.put("lastProcessedRow", fileImport.getLastProcessedRow());
        return result;

    }
}