package com.example.personmanagement.file;

import com.example.personmanagement.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final FileImportRepository fileImportRepository;

    private final FileImporter fileImporter;

    private final FileStorage fileStorage;

    public FileUploadResponse uploadFile(InputStream inputsStream, String originalFilename, long byteSize) {
        try {
            var uniqueFilename = fileStorage.save(inputsStream, originalFilename, byteSize);

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

    @Async
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
                Long batchStart = fileImport.getLastProcessedRow();
                FileImporter.Result batchResult = fileImporter.processFile(fileImport, batchStart, 2);
                fileImport.setLastProcessedRow(batchResult.lastProcessedRow());
                fileImport.setStatus(FileStatus.IN_PROGRESS);
                processing = !batchResult.isFinished();
                fileImportRepository.update(fileImport);
            }

            fileImport.setFinishedAt(LocalDateTime.now());
            fileImport.setStatus(FileStatus.SUCCESS);
        } catch (Exception e) {
            log.error("Error when processing file {}", fileImportId, e);
            fileImport.setFinishedAt(LocalDateTime.now());
            fileImport.setStatus(FileStatus.FAILED);
        }
        fileImportRepository.update(fileImport);
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