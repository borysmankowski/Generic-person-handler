package com.example.personmanagement.file;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public interface FileStorage {
    String save(InputStream inputsStream, String originalFilename, long byteSize)  throws IOException;
    BufferedReader load(String fileName) throws FileNotFoundException;
}