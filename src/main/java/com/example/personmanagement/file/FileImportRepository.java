package com.example.personmanagement.file;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileImportRepository extends JpaRepository<FileImport, Long> {

    Optional<FileImport> findFirstByStatusOrderByCreatedAtAsc(FileStatus status);
}