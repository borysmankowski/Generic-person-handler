package com.example.personmanagement.file;

import com.example.personmanagement.mapper.FileImportRowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Slf4j
public class FileImportRepository {

    private final JdbcTemplate jdbcTemplate;

    public FileImportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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

    public Optional<FileInformation> findById(Long id) throws DataAccessException {
        String sql = "SELECT * FROM file_import WHERE id = ?";
        try {
            FileInformation fileInformation = jdbcTemplate.queryForObject(sql, new Object[]{id}, new FileImportRowMapper());
            return Optional.ofNullable(fileInformation);
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