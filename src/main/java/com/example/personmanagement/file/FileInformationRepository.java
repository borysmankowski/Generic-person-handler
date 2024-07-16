package com.example.personmanagement.file;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileInformationRepository extends JpaRepository<FileInformation, Long> {
    Optional<FileInformation> findFirstByStatusOrderByCreatedAtAsc(FileStatus status);

    Optional<FileInformation> findByFilePath(String filePath);
}
