package com.example.personmanagement.file.processor;


import com.example.personmanagement.exception.DuplicateResourceException;
import com.example.personmanagement.exception.ResourceNotFoundException;
import com.example.personmanagement.file.FileInformation;
import com.example.personmanagement.file.FileInformationRepository;
import com.example.personmanagement.file.FileStatus;
import com.example.personmanagement.utils.TransactionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
@Component
@Slf4j
public class FileQueueProcessor {
    private final FileInformationRepository fileInformationRepository;
    private final FileProcessor fileProcessor;
    private final TransactionHandler transactionHandler;

    public Optional<Long> findFileToProcess() {
        return fileInformationRepository.findFirstByStatusOrderByCreatedAtAsc(FileStatus.PENDING)
                .map(FileInformation::getId);
    }

    public void processFileQueue(Long fileImportId) {
        final long batchSize = 20000;
        FileInformation fileInformation = fileInformationRepository.findById(fileImportId)
                .orElseThrow(() -> new ResourceNotFoundException("Import file with id: " + fileImportId + " hasn't been found"));

        try {
            if (fileInformation.getStartedAt() == null) {
                fileInformation.setStartedAt(LocalDateTime.now());
                fileInformation.setStatus(FileStatus.IN_PROGRESS);
                fileInformationRepository.save(fileInformation);
            }

            AtomicBoolean processing = new AtomicBoolean(true);
            while (processing.get()) {
                try {
                    transactionHandler.executeInTransaction(() -> {
                        Long batchStart = fileInformation.getLastProcessedRow();
                        FileProcessor.Result batchResult;
                        try {
                            batchResult = fileProcessor.processFile(fileInformation, batchStart, batchSize);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        fileInformation.setLastProcessedRow(batchResult.lastProcessedRow());
                        fileInformationRepository.save(fileInformation);

                        if (batchResult.isFinished()) {
                            fileInformation.setFinishedAt(LocalDateTime.now());
                            fileInformation.setStatus(FileStatus.SUCCESS);
                            fileInformationRepository.save(fileInformation);
                            processing.set(false);
                        }
                    });
                } catch (DuplicateResourceException e) {
                    log.error("Duplicate PESEL found when processing file {}", fileImportId, e);
                    fileInformation.setFinishedAt(LocalDateTime.now());
                    fileInformation.setStatus(FileStatus.FAILED);
                    fileInformationRepository.save(fileInformation);
                    throw e;
                } catch (Exception e) {
                    log.error("Error when processing file {}", fileImportId, e);
                    fileInformation.setFinishedAt(LocalDateTime.now());
                    fileInformation.setStatus(FileStatus.FAILED);
                    fileInformationRepository.save(fileInformation);
                    throw new RuntimeException(e);
                }
            }
        } catch (Exception e) {
            log.error("Error when processing file {}", fileImportId, e);
            fileInformation.setFinishedAt(LocalDateTime.now());
            fileInformation.setStatus(FileStatus.FAILED);
            fileInformationRepository.save(fileInformation);
        }
    }
}
