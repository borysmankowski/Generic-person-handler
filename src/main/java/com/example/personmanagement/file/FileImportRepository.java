package com.example.personmanagement.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@Slf4j
public class FileImportRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FileImportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(FileImport fileImport) {
        String sql = "INSERT INTO file_import (file_path, last_processed_row, status, created_at, version) VALUES (?, ?, ?, ?, ?)";
        try {
            jdbcTemplate.update(sql, fileImport.getFilePath(), fileImport.getLastProcessedRow(),
                    fileImport.getStatus().toString(), LocalDateTime.now(), fileImport.getVersion());
        } catch (DataAccessException e) {
            log.error("Failed to save file import information in the database", e);
        }
    }

    public Optional<Long> findFirstByStatusOrderByCreatedAtAsc() {
        String sql = "SELECT id FROM file_import WHERE status = ? ORDER BY created_at ASC LIMIT 1";
        try {
            Long fileId = jdbcTemplate.queryForObject(sql, new Object[]{FileStatus.PENDING.toString()}, Long.class);
            return Optional.ofNullable(fileId);
        } catch (DataAccessException e) {
            log.error("The File Id is empty!");
            return Optional.empty();
        }
    }

    public Optional<FileImport> findById(Long id) {
        String sql = "SELECT * FROM file_import WHERE id = ?";
        try {
            FileImport fileImport = jdbcTemplate.queryForObject(sql, new Object[]{id}, new FileImportRowMapper());
            return Optional.ofNullable(fileImport);
        } catch (DataAccessException e) {
            // Handle any potential exceptions or empty result sets here
            return Optional.empty();
        }
    }
}