package com.example.personmanagement.file;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Component
public class FileProcessor {

    private final FileService fileService;

    @Scheduled(cron = "${spring.tasks.scheduled.cron}")
    public void processFile() {
        CompletableFuture<Optional<Long>> resultFuture = fileService.findFileToProcess();

        resultFuture.thenApply(optionalId -> {
            optionalId.ifPresent(fileService::processFile);
            return optionalId;
        });
    }
}