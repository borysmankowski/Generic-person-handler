package com.example.personmanagement.file;

import com.example.personmanagement.exception.ResourceNotFoundException;
import com.example.personmanagement.file.processor.FileQueueAsyncProcessor;
import com.example.personmanagement.file.storage.FileStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final FileStorage fileStorage;
    private final FileQueueAsyncProcessor fileQueueAsyncProcessor;
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
            fileQueueAsyncProcessor.processFileQueue();

            return new FileUploadResponse("File uploaded successfully. File name: " + uniqueFilename, uniqueFilename);
        } catch (IOException e) {
            log.error("Failed to upload the file", e);
            return new FileUploadResponse("Failed to upload the file.", originalFilename);
        }
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

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        fileQueueAsyncProcessor.processFileQueue();
    }
}