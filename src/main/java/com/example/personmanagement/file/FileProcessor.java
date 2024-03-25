package com.example.personmanagement.file;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.integration.jdbc.lock.DefaultLockRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
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
        try {
            if (lock.tryLock(60, TimeUnit.MINUTES)) {
                try {
                    Optional<Long> optionalId = fileService.findFileToProcess();
                    optionalId.ifPresent(fileService::processFile);
                } finally {
                    lock.unlock();
                }
            } else {
                throw new InterruptedException("The lock acquisition has timed out!");
            }
        } catch (InterruptedException e) {
            throw new InterruptedException("Lock acquisition interrupted!");
        }
    }
}


