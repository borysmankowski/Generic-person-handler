package com.example.personmanagement.service;

import com.example.personmanagement.exception.FileImportException;
import com.example.personmanagement.exception.ResourceNotFoundException;
import com.example.personmanagement.file.processor.FileQueueAsyncProcessor;
import com.example.personmanagement.file.storage.FileStorage;
import com.example.personmanagement.model.file.FileImportStatusResponse;
import com.example.personmanagement.model.file.FileInformation;
import com.example.personmanagement.model.file.FileStatus;
import com.example.personmanagement.model.file.FileUploadResponse;
import com.example.personmanagement.repository.FileInformationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final FileStorage fileStorage;
    private final FileQueueAsyncProcessor fileQueueAsyncProcessor;
    private final FileInformationRepository fileInformationRepository;

    public FileUploadResponse uploadFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        long byteSize = file.getSize();

        if (byteSize <= 0) {
            throw new FileImportException("Error occurred when uploading a file");
        }

        try (InputStream inputStream = file.getInputStream()) {
            String uniqueFilename = fileStorage.save(inputStream, originalFilename, byteSize);

            FileInformation fileInformation = FileInformation.builder()
                    .filePath(uniqueFilename)
                    .lastProcessedRow(0L)
                    .status(FileStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .build();

            FileInformation savedFileInformation = fileInformationRepository.save(fileInformation);

            fileQueueAsyncProcessor.processFileQueue();

            return new FileUploadResponse(savedFileInformation.getId(), "File uploaded successfully. File name: " + uniqueFilename, uniqueFilename);
        } catch (IOException e) {
            log.error("Failed to upload the file", e);
            throw new FileImportException("Failed to upload the file.");
        }
    }

    public FileImportStatusResponse getFileImportStatus(Long id) {
        return fileInformationRepository.findById(id)
                .map(this::buildStatusResponse)
                .orElseThrow(() -> new ResourceNotFoundException("File import status not found!"));
    }

    private FileImportStatusResponse buildStatusResponse(FileInformation fileInformation) {
        return new FileImportStatusResponse(
                fileInformation.getStatus(),
                fileInformation.getCreatedAt(),
                fileInformation.getStartedAt(),
                fileInformation.getFinishedAt(),
                fileInformation.getLastProcessedRow());
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        fileQueueAsyncProcessor.processFileQueue();
    }
}