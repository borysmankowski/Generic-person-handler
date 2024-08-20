package com.example.personmanagement.file.processor;

import com.example.personmanagement.exception.DuplicateResourceException;
import com.example.personmanagement.exception.ResourceNotFoundException;
import com.example.personmanagement.file.storage.FileStorage;
import com.example.personmanagement.model.file.FileInformation;
import com.example.personmanagement.model.file.FileStatus;
import com.example.personmanagement.strategy.FileImportStrategyFacade;
import com.example.personmanagement.strategy.PersonFileImportStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileProcessor {

    private final FileImportStrategyFacade fileImportStrategyFacade;
    private final FileStorage fileStorage;
    private final JdbcTemplate jdbcTemplate;

    public Result processFile(FileInformation fileInformation, long batchSize) {
        AtomicInteger processedLines = new AtomicInteger();
        List<String[]> batchData = new ArrayList<>();
        long currentLine = fileInformation.getLastProcessedRow();

        try (BufferedReader reader = fileStorage.load(fileInformation.getFilePath())) {
            String line;
            reader.readLine();

            String[] data;
            while ((line = reader.readLine()) != null && processedLines.get() < batchSize) {
                data = line.split(",");
                batchData.add(data);
                processedLines.getAndIncrement();

                if (batchData.size() >= batchSize) {
                    bulkInsert(batchData);
                    batchData.clear();
                }
            }

            if (!batchData.isEmpty()) {
                bulkInsert(batchData);
                batchData.clear();
            }

        } catch (IOException e) {
            fileInformation.setFinishedAt(LocalDateTime.now());
            fileInformation.setStatus(FileStatus.FAILED);
            fileStorage.saveProgress(fileInformation);
            throw new RuntimeException("Error reading file: " + fileInformation.getFilePath(), e);

        } catch (DuplicateKeyException e) {
            fileInformation.setFinishedAt(LocalDateTime.now());
            fileInformation.setStatus(FileStatus.FAILED);
            fileStorage.saveProgress(fileInformation);
            throw new DuplicateResourceException("Duplicate resource found while processing file: " + fileInformation.getFilePath());
        }

        boolean isFinished = processedLines.get() < batchSize;
        return new Result(currentLine + processedLines.get(), isFinished);
    }

    private void bulkInsert(List<String[]> batchData) {
        Map<String, List<String[]>> groupedData = batchData.stream()
                .collect(Collectors.groupingBy(data -> data[0].toLowerCase() + "FileImportStrategy"));

        groupedData.forEach((strategyKey, dataList) -> {
            PersonFileImportStrategy strategy = fileImportStrategyFacade.getStrategy(strategyKey);
            if (strategy != null) {
                strategy.bulkInsert(dataList, jdbcTemplate);
            } else {
                throw new ResourceNotFoundException("Unknown type: " + strategyKey);
            }
        });
    }

    public record Result(long lastProcessedRow, boolean isFinished) {
    }
}