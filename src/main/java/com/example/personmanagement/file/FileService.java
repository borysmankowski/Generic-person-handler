
package com.example.personmanagement.file;

import com.amazonaws.AmazonClientException;
import com.amazonaws.ResetException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.personmanagement.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final FileImportRepository fileImportRepository;

    private final AmazonS3 amazonS3;

    @Value("${aws.bucketName}")
    private String bucketName;

    private final FileImporter fileImporter;

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

    public Optional<Long> findFileToProcess() {
        return fileImportRepository.findFirstByStatusOrderByCreatedAtAsc(FileStatus.PENDING)
                .map(FileImport::getId);

    }

    public void processFile(Long fileImportId) {
        FileImport fileImport = fileImportRepository.findById(fileImportId)
                .orElseThrow(() -> new ResourceNotFoundException("Import file with id: " + fileImportId + " hasnt been found"));
        try {
            if (fileImport.getStartedAt() == null) {
                fileImport.setStartedAt(LocalDateTime.now());
            }
            fileImporter.processFileWithLoad(fileImport, (lastProcessedRow) -> onRowProcessed((long) lastProcessedRow, fileImport));
            fileImport.setFinishedAt(LocalDateTime.now());
            fileImport.setStatus(FileStatus.SUCCESS);
            fileImportRepository.save(fileImport);

        } catch (Exception e) {
            log.error("Error when processing file {}", fileImportId, e);
            fileImport.setFinishedAt(LocalDateTime.now());
            fileImport.setStatus(FileStatus.FAILED);
            fileImportRepository.save(fileImport);
        }
    }

    private void onRowProcessed(long lastProcessedRow, FileImport fileImport) {
        fileImport.setLastProcessedRow(lastProcessedRow);
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