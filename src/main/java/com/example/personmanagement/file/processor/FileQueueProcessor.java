package com.example.personmanagement.file.processor;

import com.example.personmanagement.exception.DuplicateResourceException;
import com.example.personmanagement.exception.ResourceNotFoundException;
import com.example.personmanagement.file.storage.FileBatchProcessingProperties;
import com.example.personmanagement.file.storage.FileStorage;
import com.example.personmanagement.model.file.FileInformation;
import com.example.personmanagement.model.file.FileStatus;
import com.example.personmanagement.repository.FileInformationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@Slf4j
public class FileQueueProcessor {

    private final FileInformationRepository fileInformationRepository;
    private final FileProcessor fileProcessor;
    private final FileBatchProcessingProperties fileBatchProcessingProperties;
    private final Clock clock;

    private final FileStorage fileStorage;

    public FileQueueProcessor(FileInformationRepository fileInformationRepository, FileProcessor fileProcessor, FileBatchProcessingProperties fileBatchProcessingProperties, Clock clock, FileStorage fileStorage) {
        this.fileInformationRepository = fileInformationRepository;
        this.fileProcessor = fileProcessor;
        this.fileBatchProcessingProperties = fileBatchProcessingProperties;
        this.clock = clock;
        this.fileStorage = fileStorage;
    }

    public Optional<Long> findFileToProcess() {
        return fileInformationRepository.findFirstByStatusOrderByCreatedAtAsc(FileStatus.PENDING).map(FileInformation::getId);
    }

    @Transactional
    public void processFileQueue(Long fileImportId) {
        FileInformation fileInformation = fileInformationRepository.findById(fileImportId).orElseThrow(() -> new ResourceNotFoundException("Import file with id: " + fileImportId + " hasn't been found"));
        try {
            fileInformation.setStartedAt(LocalDateTime.now(clock));

            boolean processing = true;
            while (processing) {
                FileProcessor.Result batchResult = fileProcessor.processFile(fileInformation, fileBatchProcessingProperties.getBatchSize());
                fileInformation.setLastProcessedRow(batchResult.lastProcessedRow());
                fileInformation.setStatus(FileStatus.IN_PROGRESS);
                processing = !batchResult.isFinished();
                fileStorage.saveProgress(fileInformation);
            }

            fileInformation.setFinishedAt(LocalDateTime.now(clock));
            fileInformation.setStatus(FileStatus.SUCCESS);

        } catch (DuplicateKeyException e) {
            log.error("Error when processing file {}", fileImportId, e);
            fileInformation.setFinishedAt(LocalDateTime.now(clock));
            fileInformation.setStatus(FileStatus.FAILED);
            fileStorage.saveProgress(fileInformation);
            throw new DuplicateResourceException("Duplicated resource!");
        }
        fileStorage.saveProgress(fileInformation);
    }
}