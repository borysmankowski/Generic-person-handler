package com.example.personmanagement.repository;

import com.example.personmanagement.model.file.FileInformation;
import com.example.personmanagement.model.file.FileStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileInformationRepository extends JpaRepository<FileInformation, Long> {
    Optional<FileInformation> findFirstByStatusOrderByCreatedAtAsc(FileStatus status);
}
