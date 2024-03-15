package com.example.personmanagement.file;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Component
@ConditionalOnProperty(name = "spring.tasks.scheduled.enabled", havingValue = "true")
public class FileProcessor {

    private final FileService fileService;

    @Scheduled(cron = "${spring.tasks.scheduled.cron}")
    public void processFile() {

        // todo: https://spring.academy/guides/spring-spring-distributed-lock
        // zrób dla JDBC, nie Redisa, zeby nie musiec Redisa jeszcze na dockerze
        // tak naprwde to tryLock() bedziesz uzywal dokladnie w tym miejscu.

        Optional<Long> optionalId = fileService.findFileToProcess();

        optionalId.ifPresent(fileService::processFile);
    }
}