package com.example.personmanagement.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/file-imports")
@Slf4j
public class FileController {

    private final FileService fileService;

    @GetMapping("/{id}/status")
    public ResponseEntity<FileImportStatusResponse> getFileImportStatus(@PathVariable Long id) {
        return fileService.getFileImportStatus(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'IMPORTER')")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload a file.");
        }
        return fileService.uploadFile(file.getInputStream(), file.getOriginalFilename());
    }


}