package com.example.personmanagement.file;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.integration.jdbc.lock.DefaultLockRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.locks.Lock;

@RequiredArgsConstructor
@Component
@ConditionalOnProperty(name = "spring.tasks.scheduled.enabled", havingValue = "true")
public class FileProcessor {

    public static final String LOCK_KEY = "fileProcessLock";
    private final FileService fileService;
    private final DefaultLockRepository lockRepository;
    private final LockConfiguration lockConfiguration;

    @Scheduled(cron = "${spring.tasks.scheduled.cron}")
    public void processFile() throws InterruptedException {
        Lock lock = lockConfiguration.jdbcLockRegistry(lockRepository).obtain(LOCK_KEY);
        if (lock.tryLock()) {
            try {
                Optional<Long> optionalId = fileService.findFileToProcess();
                optionalId.ifPresent(fileService::processFile);
            } finally {
                lock.unlock();
            }
        } else {
            throw new InterruptedException("The lock has been interrupted!");
        }
    }
}


