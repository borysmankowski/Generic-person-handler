package com.example.personmanagement.file;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.personmanagement.exception.DuplicateResourceException;
import com.example.personmanagement.exception.ResourceNotFoundException;
import com.example.personmanagement.person.PersonCreationStrategy;
import com.example.personmanagement.person.PersonRepository;
import com.example.personmanagement.person.model.CreatePersonCommand;
import com.example.personmanagement.person.model.Person;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final PersonRepository personRepository;

    private final Map<String, PersonCreationStrategy> creationStrategyMap;

    private final FileImportRepository fileImportRepository;

    private final ComposedCsvFileRowToCreateCommandStrategy csvFileRowToCreateCommandStrategy;


    private final AmazonS3 amazonS3;

    public ResponseEntity<String> uploadFile(InputStream inputsStream, String originalFilename) {
        try {
            String uniqueFilename = System.currentTimeMillis() + "_" + originalFilename;

            // put file to S3
            var bucketName = "person-management-bucket"; // todo: do application.yaml
            amazonS3.putObject(bucketName, uniqueFilename, inputsStream, new ObjectMetadata());

            FileImport fileImport = FileImport.builder()
                    .filePath(uniqueFilename)
                    .lastProcessedRow(0L)
                    .status(FileStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .build();

            fileImportRepository.save(fileImport);

            return ResponseEntity.ok("File uploaded successfully. File name: " + uniqueFilename);
        } catch (AmazonClientException e) {
            return ResponseEntity.status(500).body("Failed to upload the file.");
        }
    }

    @Async
    public Optional<Long> findFileToProcess() {
        return fileImportRepository.findFirstByStatusOrderByCreatedAtAsc(FileStatus.PENDING)
                .map(FileImport::getId);
    }

//    @Transactional

    /**
     * albo bez transactional - ewentualnie nie zapisze nam się np. postep pliku, przejdziemy jeszcze raz przez te same rekordy, ale bedzie skipowal
     * albo z transactional - ale pytanie wtedy co robimy przy rollbacku (jesli Pesel just zduplikowany)? Moglibysmy zapisac, ze fail i juz nigdy nie przetwarzac pliku
     */
    public void processFile(Long fileImportId) {
        FileImport fileImport = fileImportRepository.findById(fileImportId)
                .orElseThrow(() -> new ResourceNotFoundException("Import file with id: " + fileImportId + " hasnt been found"));
        String filePath = fileImport.getFilePath();

        if (fileImport.getStartedAt() == null) {
            fileImport.setStartedAt(LocalDateTime.now());
        }

        var getObjectResult = amazonS3.getObject("person-management-bucket", filePath);
        var fileInputStream = getObjectResult.getObjectContent();
        var reader = new BufferedReader(new InputStreamReader(fileInputStream));
        try (var lines = reader.lines()) {
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
        } catch (Exception e) {
            fileImport.setStatus(FileStatus.FAILED);
            log.error("Error when processing file");
        }
    }

    private void processFileLine(String line) {
        String[] data = line.split(",");
        String type = data[0];
        PersonCreationStrategy strategy = creationStrategyMap.get(type);

        if (strategy != null) {
            try {
                createAndAddToDatabase(strategy, data);
            } catch (DuplicateResourceException e) { //todo: handle invalid pesel
                log.warn("Skipping line due to duplicate Pesel: {}", line);
            } catch (IllegalArgumentException e) {
                log.warn("Skipping line due to invalid Pesel: {}", line);
            } catch (DataIntegrityViolationException e) {
                log.warn("Skipping line due to invalid Pesel: {}", line);
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

    public ResponseEntity<FileImportStatusResponse> getFileImportStatus(Long id) {
        Optional<FileImport> fileImportOptional = fileImportRepository.findById(id);
        return fileImportOptional.map(this::buildStatusResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private FileImportStatusResponse buildStatusResponse(FileImport fileImport) {
        return new FileImportStatusResponse(
                fileImport.getStatus(),
                fileImport.getCreatedAt(),
                fileImport.getStartedAt(),
                fileImport.getLastProcessedRow());
    }
}