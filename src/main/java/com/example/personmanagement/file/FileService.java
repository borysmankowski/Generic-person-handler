package com.example.personmanagement.file;

import com.example.personmanagement.exception.ResourceNotFoundException;
import com.example.personmanagement.file.processor.FileImporter;
import com.example.personmanagement.file.processor.FileProcessor;
import com.example.personmanagement.file.storage.FileStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final FileImportRepository fileImportRepository;

    private final FileProcessor fileProcessor;

    private final FileStorage fileStorage;

    private final FileImporter fileImporter;

    public FileUploadResponse uploadFile(InputStream inputStream, String originalFilename, long byteSize) {
        if (byteSize <= 0) {
            return new FileUploadResponse("Error occurred when uploading a file", originalFilename);
        }
        try {
            String uniqueFilename = fileStorage.save(inputStream, originalFilename, byteSize);

            FileInformation fileInformation = FileInformation.builder()
                    .filePath(uniqueFilename)
                    .lastProcessedRow(0L)
                    .status(FileStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .build();

            fileImporter.insert(fileInformation);

            FileUploadResponse response = new FileUploadResponse("File uploaded successfully. File name: " + uniqueFilename, uniqueFilename);
            return response;
        } catch (IOException e) {
            log.error("Failed to upload the file", e);
            return new FileUploadResponse("Failed to upload the file.", originalFilename);
        }
    }

    @Async
    public Optional<Long> findFileToProcess() {
        return fileImportRepository.findFirstByStatusOrderByCreatedAtAsc();

    }

    public void processFile(Long fileImportId) {
        FileInformation fileInformation = fileImportRepository.findById(fileImportId)
                .orElseThrow(() -> new ResourceNotFoundException("Import file with id: " + fileImportId + " hasnt been found"));
        try {
            if (fileInformation.getStartedAt() == null) {
                fileInformation.setStartedAt(LocalDateTime.now());
            }

            var processing = true;

            while (processing) {
                Long batchStart = fileInformation.getLastProcessedRow();
                FileProcessor.Result batchResult = fileProcessor.processFile(fileInformation, batchStart, 7);
                fileInformation.setLastProcessedRow(batchResult.lastProcessedRow());
                fileInformation.setStatus(FileStatus.IN_PROGRESS);
                processing = !batchResult.isFinished();
                fileImporter.update(fileInformation);
            }

            fileInformation.setFinishedAt(LocalDateTime.now());
            fileInformation.setStatus(FileStatus.SUCCESS);
        } catch (Exception e) {
            log.error("Error when processing file {}", fileImportId, e);
            fileInformation.setFinishedAt(LocalDateTime.now());
            fileInformation.setStatus(FileStatus.FAILED);
        }
        fileImporter.update(fileInformation);
    }

    public FileImportStatusResponse getFileImportStatus(Long id) {
        Optional<FileInformation> fileImportOptional = fileImportRepository.findById(id);
        return fileImportOptional.map(this::buildStatusResponse)
                .orElseThrow(() -> new ResourceNotFoundException("File import status not found!"));
    }

    private FileImportStatusResponse buildStatusResponse(FileInformation fileInformation) {
        return new FileImportStatusResponse(
                fileInformation.getStatus(),
                fileInformation.getCreatedAt(),
                fileInformation.getStartedAt(),
                fileInformation.getLastProcessedRow());
    }
}