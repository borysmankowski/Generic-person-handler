package com.example.personmanagement.controller;

import com.example.personmanagement.model.file.FileImportStatusResponse;
import com.example.personmanagement.model.file.FileUploadResponse;
import com.example.personmanagement.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/file-imports")
@Slf4j
public class FileController {

    private final FileService fileService;

    @GetMapping("/{id}")
    public ResponseEntity<FileImportStatusResponse> getFileImportStatus(@PathVariable Long id) {
        return ResponseEntity.ok(fileService.getFileImportStatus(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'IMPORTER')")
    public ResponseEntity<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        FileUploadResponse uploadResponse = fileService.uploadFile(file);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(uploadResponse);
    }
}