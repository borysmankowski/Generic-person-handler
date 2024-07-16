package com.example.personmanagement.file.processor;

import com.example.personmanagement.file.LockConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.jdbc.lock.DefaultLockRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;

@RequiredArgsConstructor
@Component
@Slf4j
public class FileQueueAsyncProcessor {
  private final FileQueueProcessor fileQueueProcessor;
  public static final String LOCK_KEY = "fileProcessLock";
  private final DefaultLockRepository lockRepository;
  private final LockConfiguration lockConfiguration;

  @Async
  public void processFileQueue() {
    Lock lock = lockConfiguration.jdbcLockRegistry(lockRepository).obtain(LOCK_KEY);
    if (lock.tryLock()) {
      try {
        var fileId = fileQueueProcessor.findFileToProcess();
        while (fileId.isPresent()) {
          fileId.ifPresent(fileQueueProcessor::processFileQueue);
          fileId = fileQueueProcessor.findFileToProcess();
        }
      } finally {
        lock.unlock();
      }
    } else {
      log.info("Queue processing already in place");
    }
  }
}