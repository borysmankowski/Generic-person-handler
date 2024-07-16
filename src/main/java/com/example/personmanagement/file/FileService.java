package com.example.personmanagement.file;

import com.example.personmanagement.exception.DuplicateResourceException;
import com.example.personmanagement.exception.ResourceNotFoundException;
import com.example.personmanagement.file.processor.FileImporter;
import com.example.personmanagement.file.processor.FileProcessor;
import com.example.personmanagement.file.storage.FileStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final FileProcessor fileProcessor;

    private final FileStorage fileStorage;

    private final FileInformationRepository fileInformationRepository;

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

            fileInformationRepository.save(fileInformation);

            return new FileUploadResponse("File uploaded successfully. File name: " + uniqueFilename, uniqueFilename);
        } catch (IOException e) {
            log.error("Failed to upload the file", e);
            return new FileUploadResponse("Failed to upload the file.", originalFilename);
        }
    }

    public Optional<Long> findFileToProcess() {
        return fileInformationRepository.findFirstByStatusOrderByCreatedAtAsc(FileStatus.PENDING)
                .map(FileInformation::getId);
    }

    public void processFile(Long fileImportId) {
        final long batchSize = 20000;
        FileInformation fileInformation = fileInformationRepository.findById(fileImportId)
                .orElseThrow(() -> new ResourceNotFoundException("Import file with id: " + fileImportId + " hasn't been found"));
        try {
            if (fileInformation.getStartedAt() == null) {
                fileInformation.setStartedAt(LocalDateTime.now());
            }

            boolean processing = true;

            while (processing) {
                Long batchStart = fileInformation.getLastProcessedRow();
                FileProcessor.Result batchResult = fileProcessor.processFile(fileInformation, batchStart, batchSize);
                fileInformation.setLastProcessedRow(batchResult.lastProcessedRow());
                fileInformation.setStatus(FileStatus.IN_PROGRESS);
                processing = !batchResult.isFinished();
                fileInformationRepository.save(fileInformation);
            }

            fileInformation.setFinishedAt(LocalDateTime.now());
            fileInformation.setStatus(FileStatus.SUCCESS);

        } catch (DuplicateResourceException e) {
            log.error("Duplicate PESEL found when processing file {}", fileImportId, e);
            fileInformation.setFinishedAt(LocalDateTime.now());
            fileInformation.setStatus(FileStatus.FAILED);
            fileInformationRepository.save(fileInformation);

        } catch (Exception e) {
            log.error("Error when processing file {}", fileImportId, e);
            fileInformation.setFinishedAt(LocalDateTime.now());
            fileInformation.setStatus(FileStatus.FAILED);
            fileInformationRepository.save(fileInformation);
        }
        fileInformationRepository.save(fileInformation);
    }

    public FileImportStatusResponse getFileImportStatus(Long id) {
        Optional<FileInformation> fileImportOptional = fileInformationRepository.findById(id);
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