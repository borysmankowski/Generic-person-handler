package com.example.personmanagement.file;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileImportStatusResponse {

    private FileStatus status;
    private LocalDateTime createdDate;
    private LocalDateTime startDate;
    private Long lastProcessedRow;
}