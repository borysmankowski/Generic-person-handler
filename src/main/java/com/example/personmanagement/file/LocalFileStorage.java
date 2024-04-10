package com.example.personmanagement.file;

import org.springframework.beans.factory.annotation.Value;
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

    @Value("${spring.upload.dir}")
    private String UPLOAD_DIR;

    @Override
    public String save(InputStream inputsStream, String originalFilename, long byteSize) throws IOException {
        String uniqueFilename = System.currentTimeMillis() + "_" + originalFilename;
        Path filePath = Path.of(UPLOAD_DIR, uniqueFilename);
        Files.copy(inputsStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFilename;
    }

    @Override
    public BufferedReader load(String fileName) throws FileNotFoundException {
        Path filePath = Path.of(UPLOAD_DIR, fileName);
        return new BufferedReader(new InputStreamReader(new FileInputStream(String.valueOf(filePath))));
    }

}