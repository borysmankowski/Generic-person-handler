package com.example.personmanagement.file.processor;

import com.example.personmanagement.exception.DuplicateResourceException;
import com.example.personmanagement.exception.ResourceNotFoundException;
import com.example.personmanagement.file.storage.FileStorage;
import com.example.personmanagement.model.file.FileInformation;
import com.example.personmanagement.repository.FileInformationRepository;
import com.example.personmanagement.strategy.FileImportStrategyFacade;
import com.example.personmanagement.strategy.PersonFileImportStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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

    private final FileImportStrategyFacade fileImportStrategyFacade;
    private final FileStorage fileStorage;
    private final JdbcTemplate jdbcTemplate;
    private final FileInformationRepository fileInformationRepository;

    public Result processFile(FileInformation fileInformation, long batchSize) throws IOException, DuplicateResourceException {
        AtomicInteger processedLines = new AtomicInteger();
        List<String[]> batchData = new ArrayList<>();
        long currentLine = fileInformation.getLastProcessedRow();

        try (BufferedReader reader = fileStorage.load(fileInformation.getFilePath())) {
            String line;
            reader.readLine();

            String[] data;
            while ((line = reader.readLine()) != null) {
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveProgress(FileInformation fileInformation) {
        fileInformationRepository.save(fileInformation);
    }

    public record Result(long lastProcessedRow, boolean isFinished) {
    }
}