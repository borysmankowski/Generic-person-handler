package com.example.personmanagement.model.file;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FileUploadResponse {
    private Long id;
    private String message;
    private String fileName;
}
