package com.example.personmanagement.file.processor;

import com.example.personmanagement.exception.ResourceNotFoundException;
import com.example.personmanagement.file.FileInformation;
import com.example.personmanagement.file.storage.FileStorage;
import com.example.personmanagement.person.model.PersonFileImportStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileProcessor {

    public record Result(long lastProcessedRow, boolean isFinished) {
    }

    private final Map<String, PersonFileImportStrategy> fileImportStrategyMap;

    private final FileStorage fileStorage;

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public Result processFile(FileInformation fileInformation, long batchStart, long batchSize) throws IOException {
        AtomicInteger processedLines = new AtomicInteger();
        List<String[]> batchData = new ArrayList<>();
        long currentLine = 0;

        try (BufferedReader reader = fileStorage.load(fileInformation.getFilePath())) {
            String line;
            reader.readLine();

            while (currentLine < batchStart && reader.readLine() != null) {
                currentLine++;
            }

            while ((line = reader.readLine()) != null && processedLines.get() < batchSize) {
                String[] data = line.split(",");
                batchData.add(data);
                processedLines.getAndIncrement();

                if (batchData.size() >= 20000) {
                    bulkInsert(batchData);
                    batchData.clear();
                }
            }

            if (!batchData.isEmpty()) {
                bulkInsert(batchData);
                batchData.clear();
            }
        }
        boolean isFinished = processedLines.get() < batchSize;
        return new Result(batchStart + processedLines.get(), isFinished);
    }


    private void processFileLine(String line) {
        String[] data = line.split(",");
        String type = data[0];
        String key = type.toLowerCase() + "FileImportStrategy";
        PersonFileImportStrategy strategy = fileImportStrategyMap.get(key);

        if (strategy != null) {
            strategy.insert(data, jdbcTemplate);
        } else {
            throw new ResourceNotFoundException("Unknown type: " + type);
        }
    }

    private void bulkInsert(List<String[]> batchData) {
        Map<String, List<String[]>> groupedData = batchData.stream()
                .collect(Collectors.groupingBy(data -> data[0].toLowerCase() + "FileImportStrategy"));

        groupedData.forEach((strategyKey, dataList) -> {
            PersonFileImportStrategy strategy = fileImportStrategyMap.get(strategyKey);
            if (strategy != null) {
                strategy.bulkInsert(dataList, jdbcTemplate);
            } else {
                throw new ResourceNotFoundException("Unknown type: " + strategyKey);
            }
        });
    }
}