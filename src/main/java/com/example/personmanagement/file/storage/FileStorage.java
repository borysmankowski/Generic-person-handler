package com.example.personmanagement.file.storage;

import com.example.personmanagement.model.file.FileInformation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public interface FileStorage {
    String save(InputStream inputsStream, String originalFilename, long byteSize) throws IOException;

    BufferedReader load(String fileName) throws FileNotFoundException;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void saveProgress(FileInformation fileInformation);
}