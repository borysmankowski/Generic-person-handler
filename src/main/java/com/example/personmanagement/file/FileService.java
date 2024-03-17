
package com.example.personmanagement.file;

import com.amazonaws.AmazonClientException;
import com.amazonaws.ResetException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.example.personmanagement.exception.DuplicateResourceException;
import com.example.personmanagement.exception.ResourceNotFoundException;
import com.example.personmanagement.person.PersonCreationStrategy;
import com.example.personmanagement.person.PersonRepository;
import com.example.personmanagement.person.model.CreatePersonCommand;
import com.example.personmanagement.person.model.Person;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final PersonRepository personRepository;

    private final Map<String, PersonCreationStrategy> creationStrategyMap;

    private final FileImportRepository fileImportRepository;

    private final ComposedCsvFileRowToCreateCommandStrategy csvFileRowToCreateCommandStrategy;


    private final AmazonS3 amazonS3;

    @Value("${aws.bucketName}")
    private String bucketName;

    public ResponseEntity<String> uploadFile(InputStream inputsStream, String originalFilename) {
        try {
            String uniqueFilename = System.currentTimeMillis() + "_" + originalFilename;

            amazonS3.putObject(bucketName, uniqueFilename, inputsStream, new ObjectMetadata());

            FileImport fileImport = FileImport.builder()
                    .filePath(uniqueFilename)
                    .lastProcessedRow(0L)
                    .status(FileStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .build();

            fileImportRepository.save(fileImport);

            return ResponseEntity.ok("File uploaded successfully. File name: " + uniqueFilename);
        } catch (ResetException e) {
            log.error("Failed to upload the file {}", e.getExtraInfo(), e);
            return ResponseEntity.status(500).body("Failed to upload the file.");
        } catch (AmazonClientException e) {
            log.error("Failed to upload the file ", e);
            return ResponseEntity.status(500).body("Failed to upload the file.");
        }
    }

    @Async
    public Optional<Long> findFileToProcess() {
        return fileImportRepository.findFirstByStatusOrderByCreatedAtAsc(FileStatus.PENDING)
                .map(FileImport::getId);

    }

    public void processFile(Long fileImportId) {
        FileImport fileImport = fileImportRepository.findById(fileImportId)
                .orElseThrow(() -> new ResourceNotFoundException("Import file with id: " + fileImportId + " hasnt been found"));
        String filePath = fileImport.getFilePath();

        if (fileImport.getStartedAt() == null) {
            fileImport.setStartedAt(LocalDateTime.now());
        }

        S3Object getObjectResult = amazonS3.getObject("person-management-bucket", filePath);
        S3ObjectInputStream fileInputStream = getObjectResult.getObjectContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
        String currentLine = null;

        try (var lines = reader.lines()) {
            Stream<String> batchLines = lines.skip(1).skip(fileImport.getLastProcessedRow());
            Iterator<String> iterator = batchLines.iterator();
            int processedLines = 0;
            try {
                while (iterator.hasNext()) {
                    currentLine = iterator.next();
                    processFileLine(currentLine);
                    processedLines++;
                }
                fileImport.setStatus(FileStatus.SUCCESS);
            } catch (Exception e) {
                log.error("Error when processing file {}. Line: {}", fileImport.getId(), currentLine, e);
                fileImport.setStatus(FileStatus.FAILED);
            } finally {
                fileImport.setLastProcessedRow(fileImport.getLastProcessedRow() + processedLines);
                fileImport.setFinishedAt(LocalDateTime.now());
                fileImportRepository.save(fileImport);
            }
        } catch (Exception e) {
            log.error("Error when processing file {}. Line: {}", fileImport.getId(), currentLine, e);
            fileImport.setStatus(FileStatus.FAILED);
        }
    }

    private void processFileLine(String line) {
        String[] data = line.split(",");
        String type = data[0];
        PersonCreationStrategy strategy = creationStrategyMap.get(type);

        if (strategy != null) {
            try {
                createAndAddToDatabase(strategy, data);
            } catch (DuplicateResourceException | DataIntegrityViolationException e) {
                log.warn("Skipping line due to duplicate Pesel: {}", line, e);
            } catch (IllegalArgumentException e) {
                log.warn("Skipping line due to invalid Pesel: {}", line, e);
            } catch (TransactionSystemException e) {
                log.warn("Skipping line {} due to: {}", line, e.getMessage(), e);
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
