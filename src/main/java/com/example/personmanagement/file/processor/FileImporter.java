package com.example.personmanagement.file.processor;

import com.example.personmanagement.file.FileInformation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FileImporter {

    private final JdbcTemplate jdbcTemplate;

    public FileImporter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(FileInformation fileInformation) throws DataAccessException {
        String sql = "INSERT INTO file_import (file_path, last_processed_row, status, created_at) VALUES (?, ?, ?, ?)";
        try {
            jdbcTemplate.update(sql, fileInformation.getFilePath(), fileInformation.getLastProcessedRow(),
                    fileInformation.getStatus().toString(), fileInformation.getCreatedAt());
        } catch (DataAccessException e) {
            log.error("Error occurred while inserting file import data: {}", e.getMessage());
            throw e;
        }
    }

    public void update(FileInformation fileInformation) throws DataAccessException {
        String sql = """
                UPDATE file_import
                SET
                    file_path = ?,
                    last_processed_row = ?,
                    status = ?,
                    finished_at = ?,
                    started_at = ?
                WHERE id = ?
                """;
        try {
            jdbcTemplate.update(sql, fileInformation.getFilePath(), fileInformation.getLastProcessedRow(),
                    fileInformation.getStatus().toString(), fileInformation.getFinishedAt(), fileInformation.getStartedAt(), fileInformation.getId());
        } catch (DataAccessException e) {
            log.error("Error occurred while updating file import data: {}", e.getMessage());
            throw e;
        }
    }
}