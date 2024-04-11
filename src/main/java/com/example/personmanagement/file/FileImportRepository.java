package com.example.personmanagement.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Slf4j
public class FileImportRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FileImportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(FileImport fileImport) throws DataAccessException {
        String sql = "INSERT INTO file_import (file_path, last_processed_row, status, created_at) VALUES (?, ?, ?, ?)";
        try {
            jdbcTemplate.update(sql, fileImport.getFilePath(), fileImport.getLastProcessedRow(),
                    fileImport.getStatus().toString(), fileImport.getCreatedAt());
        } catch (DataAccessException e) {
            log.error("Error occurred while inserting file import data: {}", e.getMessage());
            throw e;
        }
    }

    public void update(FileImport fileImport) throws DataAccessException {
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
            jdbcTemplate.update(sql, fileImport.getFilePath(), fileImport.getLastProcessedRow(),
                    fileImport.getStatus().toString(), fileImport.getFinishedAt(), fileImport.getStartedAt(), fileImport.getId());
        } catch (DataAccessException e) {
            log.error("Error occurred while updating file import data: {}", e.getMessage());
            throw e;
        }
    }

    public Optional<Long> findFirstByStatusOrderByCreatedAtAsc() throws EmptyResultDataAccessException {
        String sql = "SELECT id FROM file_import WHERE status = ? ORDER BY created_at ASC LIMIT 1";
        try {
            Long fileId = jdbcTemplate.queryForObject(sql, new Object[]{FileStatus.PENDING.toString()}, Long.class);
            return Optional.ofNullable(fileId);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<FileImport> findById(Long id) throws DataAccessException {
        String sql = "SELECT * FROM file_import WHERE id = ?";
        try {
            FileImport fileImport = jdbcTemplate.queryForObject(sql, new Object[]{id}, new FileImportRowMapper());
            return Optional.ofNullable(fileImport);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void deleteAll() {
        String sql = "TRUNCATE TABLE file_import";
        try {
            jdbcTemplate.update(sql);
        } catch (DataAccessException e) {
            log.error("Error occurred while deleting all file import data: {}", e.getMessage());
            throw e;
        }
    }
}