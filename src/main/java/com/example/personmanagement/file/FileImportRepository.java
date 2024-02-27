package com.example.personmanagement.file;

import jakarta.persistence.LockModeType;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FileImportRepository extends JpaRepository<FileImport, Long> {

    Optional<FileImport> findFirstByStatusOrderByCreatedAtAsc(FileStatus status);
}