package com.example.personmanagement.file;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.integration.jdbc.lock.DefaultLockRepository;
import org.springframework.integration.jdbc.lock.JdbcLockRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.locks.Lock;

@RequiredArgsConstructor
@Component
@ConditionalOnProperty(name = "spring.tasks.scheduled.enabled", havingValue = "true")
public class FileProcessor {

    private final FileService fileService;
    private final DefaultLockRepository lockRepository;

    @Scheduled(cron = "${spring.tasks.scheduled.cron}")
    public void processFile() {
        String lockKey = "fileProcessLock";
        Lock lock = new JdbcLockRegistry(lockRepository).obtain(lockKey);
        if (lock.tryLock()) {
            try {
                Optional<Long> optionalId = fileService.findFileToProcess();
                optionalId.ifPresent(fileService::processFile);
            } finally {
                lock.unlock();
            }
        } else {
            System.out.println("niet good");
        }
    }
}