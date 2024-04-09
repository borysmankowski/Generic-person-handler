
package com.example.personmanagement.file;

import com.example.personmanagement.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final FileImportRepository fileImportRepository;

    private final FileImporter fileImporter;

    @Value("${spring.upload.dir}")
    private String UPLOAD_DIR;

    public FileUploadResponse uploadFile(InputStream inputsStream, String originalFilename) {
        try {
            String uniqueFilename = System.currentTimeMillis() + "_" + originalFilename;
            Path filePath = Path.of(UPLOAD_DIR, uniqueFilename);
            Files.copy(inputsStream, filePath, StandardCopyOption.REPLACE_EXISTING);

            FileImport fileImport = FileImport.builder()
                    .filePath(uniqueFilename)
                    .lastProcessedRow(0L)
                    .status(FileStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .build();

            fileImportRepository.insert(fileImport);

            return new FileUploadResponse("File uploaded successfully. File name: ", uniqueFilename);
        } catch (IOException e) {
            log.error("Failed to upload the file ", e);
            return new FileUploadResponse("Failed to upload the file. ", originalFilename);
        }
    }

    public Optional<Long> findFileToProcess() {
        return fileImportRepository.findFirstByStatusOrderByCreatedAtAsc();

    }

    public void processFile(Long fileImportId) {
        FileImport fileImport = fileImportRepository.findById(fileImportId)
                .orElseThrow(() -> new ResourceNotFoundException("Import file with id: " + fileImportId + " hasnt been found"));
        try {
            if (fileImport.getStartedAt() == null) {
                fileImport.setStartedAt(LocalDateTime.now());
            }

            var processing = true;

            while (processing) {
                var batchStart = fileImport.getLastProcessedRow();
                var batchResult = fileImporter.processFile(fileImport, batchStart, 2);
                fileImport.setLastProcessedRow(batchResult.lastProcessedRow());
                processing = !batchResult.isFinished();
            }

            fileImport.setFinishedAt(LocalDateTime.now());
            fileImport.setStatus(FileStatus.SUCCESS);
            fileImportRepository.update(fileImport);
        } catch (Exception e) {
            log.error("Error when processing file {}", fileImportId, e);
            fileImport.setFinishedAt(LocalDateTime.now());
            fileImport.setStatus(FileStatus.FAILED);
            fileImportRepository.update(fileImport);
        }
    }

    public FileImportStatusResponse getFileImportStatus(Long id) {
        Optional<FileImport> fileImportOptional = fileImportRepository.findById(id);
        return fileImportOptional.map(this::buildStatusResponse)
                .orElseThrow(() -> new ResourceNotFoundException("File import status not found!"));
    }

    private FileImportStatusResponse buildStatusResponse(FileImport fileImport) {
        return new FileImportStatusResponse(
                fileImport.getStatus(),
                fileImport.getCreatedAt(),
                fileImport.getStartedAt(),
                fileImport.getLastProcessedRow());
    }
}