package com.example.personmanagement.file;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class FileImportRowMapper implements RowMapper<FileImport> {
    @Override
    public FileImport mapRow(ResultSet rs, int rowNum) throws SQLException {
        FileImport fileImport = new FileImport();
        fileImport.setId(rs.getLong("id"));
        fileImport.setFilePath(rs.getString("file_path"));
        fileImport.setStatus(FileStatus.valueOf(rs.getString("status")));
        fileImport.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        fileImport.setStartedAt(getLocalDateTimeOrNull(rs, "started_at"));
        fileImport.setFinishedAt(getLocalDateTimeOrNull(rs, "finished_at"));
        fileImport.setLastProcessedRow(rs.getLong("last_processed_row"));
        // Map other fields as needed
        return fileImport;
    }

    private LocalDateTime getLocalDateTimeOrNull(ResultSet rs, String columnName) throws SQLException {
        java.sql.Timestamp timestamp = rs.getTimestamp(columnName);
        return timestamp != null ? timestamp.toLocalDateTime() : null;
    }
}
