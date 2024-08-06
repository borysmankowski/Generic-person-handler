package com.example.personmanagement.file.processor;

import com.example.personmanagement.exception.DuplicateResourceException;
import com.example.personmanagement.exception.ResourceNotFoundException;
import com.example.personmanagement.model.file.FileInformation;
import com.example.personmanagement.model.file.FileStatus;
import com.example.personmanagement.repository.FileInformationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@Slf4j
public class FileQueueProcessor {

    private final FileInformationRepository fileInformationRepository;
    private final FileProcessor fileProcessor;
    private final long batchSize;

    public FileQueueProcessor(
            FileInformationRepository fileInformationRepository,
            FileProcessor fileProcessor,
            @Value("${file-queue-processor.batch-size}") long batchSize) {
        this.fileInformationRepository = fileInformationRepository;
        this.fileProcessor = fileProcessor;
        this.batchSize = batchSize;
    }

    public Optional<Long> findFileToProcess() {
        return fileInformationRepository.findFirstByStatusOrderByCreatedAtAsc(FileStatus.PENDING)
                .map(FileInformation::getId);
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public void processFileQueue(Long fileImportId) {
        FileInformation fileInformation = fileInformationRepository.findById(fileImportId)
                .orElseThrow(() -> new ResourceNotFoundException("Import file with id: " + fileImportId + " hasn't been found"));
        try {
            if (fileInformation.getStartedAt() == null) {
                fileInformation.setStartedAt(LocalDateTime.now());
            }

            boolean processing = true;
            while (processing) {
                FileProcessor.Result batchResult;
                try {
                    batchResult = fileProcessor.processFile(fileInformation, batchSize);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                fileInformation.setLastProcessedRow(batchResult.lastProcessedRow());
                fileInformation.setStatus(FileStatus.IN_PROGRESS);
                processing = !batchResult.isFinished();
                fileProcessor.saveProgress(fileInformation);
            }

            fileInformation.setFinishedAt(LocalDateTime.now());
            fileInformation.setStatus(FileStatus.SUCCESS);

        } catch (DuplicateKeyException e) {
            log.error("Error when processing file {}", fileImportId, e);
            fileInformation.setFinishedAt(LocalDateTime.now());
            fileInformation.setStatus(FileStatus.FAILED);
            fileProcessor.saveProgress(fileInformation);
            throw new RuntimeException(e);
        }
        fileProcessor.saveProgress(fileInformation);
    }

}
