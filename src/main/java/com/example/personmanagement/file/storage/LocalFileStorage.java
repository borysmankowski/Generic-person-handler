package com.example.personmanagement.file.storage;

import com.example.personmanagement.repository.FileInformationRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;


@Component
@ConditionalOnProperty(prefix = "file-storage", name = "solution", havingValue = "local")
public class LocalFileStorage implements FileStorage {

    public final FileInformationRepository fileInformationRepository;
    private final FileStorageProperties fileStorageProperties;

    public LocalFileStorage(FileStorageProperties fileStorageProperties, FileInformationRepository fileInformationRepository) {
        this.fileStorageProperties = fileStorageProperties;
        this.fileInformationRepository = fileInformationRepository;
    }

    @Override
    public String save(InputStream inputsStream, String originalFilename, long byteSize) throws IOException {
        String uniqueFilename = System.currentTimeMillis() + "_" + originalFilename;
        Path filePath = Path.of(fileStorageProperties.getDir(), uniqueFilename);
        Files.copy(inputsStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFilename;
    }

    @Override
    public BufferedReader load(String fileName) throws FileNotFoundException {
        Path filePath = Path.of(fileStorageProperties.getDir(), fileName);
        return new BufferedReader(new InputStreamReader(new FileInputStream(String.valueOf(filePath))));
    }
}