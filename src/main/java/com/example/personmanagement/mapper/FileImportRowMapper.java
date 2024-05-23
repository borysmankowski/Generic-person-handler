package com.example.personmanagement.mapper;

import com.example.personmanagement.file.FileInformation;
import com.example.personmanagement.file.FileStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class FileImportRowMapper implements RowMapper<FileInformation> {
    @Override
    public FileInformation mapRow(ResultSet rs, int rowNum) throws SQLException {
        FileInformation fileInformation = new FileInformation();
        fileInformation.setId(rs.getLong("id"));
        fileInformation.setFilePath(rs.getString("file_path"));
        fileInformation.setStatus(FileStatus.valueOf(rs.getString("status")));
        fileInformation.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        fileInformation.setStartedAt(getLocalDateTimeOrNull(rs, "started_at"));
        fileInformation.setFinishedAt(getLocalDateTimeOrNull(rs, "finished_at"));
        fileInformation.setLastProcessedRow(rs.getLong("last_processed_row"));
        return fileInformation;
    }

    private LocalDateTime getLocalDateTimeOrNull(ResultSet rs, String columnName) throws SQLException {
        java.sql.Timestamp timestamp = rs.getTimestamp(columnName);
        return timestamp != null ? timestamp.toLocalDateTime() : null;
    }
}