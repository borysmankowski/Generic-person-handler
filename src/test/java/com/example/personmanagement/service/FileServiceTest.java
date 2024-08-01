package com.example.personmanagement.service;

import com.example.personmanagement.exception.ResourceNotFoundException;
import com.example.personmanagement.file.processor.FileQueueAsyncProcessor;
import com.example.personmanagement.file.storage.FileStorage;
import com.example.personmanagement.model.file.FileImportStatusResponse;
import com.example.personmanagement.model.file.FileInformation;
import com.example.personmanagement.model.file.FileStatus;
import com.example.personmanagement.model.file.FileUploadResponse;
import com.example.personmanagement.repository.FileInformationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private FileInformationRepository fileInformationRepository;

    private FileService fileService;

    @Mock
    private FileStorage fileStorage;

    @Mock
    private MultipartFile multipartFile;

    @Mock
    private FileQueueAsyncProcessor fileQueueAsyncProcessor;

    @BeforeEach
    public void setUp() {
        fileService = new FileService(fileStorage, fileQueueAsyncProcessor, fileInformationRepository);
    }

    @Test
    void uploadFile_Success() throws Exception {

        String originalFilename = "testfile.txt";
        long byteSize = 123L;
        String uniqueFilename = "unique_testfile.txt";
        InputStream inputStream = new ByteArrayInputStream("test content".getBytes());

        when(multipartFile.getOriginalFilename()).thenReturn(originalFilename);
        when(multipartFile.getSize()).thenReturn(byteSize);
        when(multipartFile.getInputStream()).thenReturn(inputStream);
        when(fileStorage.save(any(InputStream.class), eq(originalFilename), eq(byteSize)))
                .thenReturn(uniqueFilename);

        FileUploadResponse response = fileService.uploadFile(multipartFile);

        assertEquals("File uploaded successfully. File name: unique_testfile.txt", response.getMessage());
        assertEquals(uniqueFilename, response.getFileName());
        verify(fileInformationRepository).save(any(FileInformation.class));
        verify(fileQueueAsyncProcessor).processFileQueue();
    }

    @Test
    void uploadFile_FileSizeZeroOrNegative() {

        String originalFilename = "testfile.txt";
        long byteSize = 0L;

        when(multipartFile.getOriginalFilename()).thenReturn(originalFilename);
        when(multipartFile.getSize()).thenReturn(byteSize);

        FileUploadResponse response = fileService.uploadFile(multipartFile);

        assertEquals("Error occurred when uploading a file", response.getMessage());
        assertEquals(originalFilename, response.getFileName());
        verify(fileInformationRepository, never()).save(any(FileInformation.class));
        verify(fileQueueAsyncProcessor, never()).processFileQueue();
    }

    @Test
    void uploadFile_ExceptionDuringUpload() throws Exception {

        String originalFilename = "testfile.txt";
        long byteSize = 123L;
        InputStream inputStream = new ByteArrayInputStream("test content".getBytes());

        when(multipartFile.getOriginalFilename()).thenReturn(originalFilename);
        when(multipartFile.getSize()).thenReturn(byteSize);
        when(multipartFile.getInputStream()).thenReturn(inputStream);
        when(fileStorage.save(any(InputStream.class), eq(originalFilename), eq(byteSize)))
                .thenThrow(new IOException("Storage failure"));

        FileUploadResponse response = fileService.uploadFile(multipartFile);

        assertEquals("Failed to upload the file.", response.getMessage());
        assertEquals(originalFilename, response.getFileName());
        verify(fileInformationRepository, never()).save(any(FileInformation.class));
        verify(fileQueueAsyncProcessor, never()).processFileQueue();
    }

    @Test
    void getFileImportStatus_FileFound() {

        Long id = 1L;
        FileInformation fileInformation = FileInformation.builder()
                .filePath("path/to/file")
                .lastProcessedRow(10L)
                .status(FileStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .build();

        when(fileInformationRepository.findById(id)).thenReturn(Optional.of(fileInformation));

        FileImportStatusResponse response = fileService.getFileImportStatus(id);

        assertNotNull(response);
        assertEquals(fileInformation.getStatus(), response.getStatus());
        assertEquals(fileInformation.getLastProcessedRow(), response.getLastProcessedRow());
    }

    @Test
    void getFileImportStatus_FileNotFound() {

        Long id = 1L;
        when(fileInformationRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            fileService.getFileImportStatus(id);
        });
        assertEquals("File import status not found!", thrown.getMessage());
    }
}
