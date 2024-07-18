package com.example.personmanagement.strategy;

import com.example.personmanagement.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
@Component
@RequiredArgsConstructor
public class FileImportStrategyFacade {

    private final Map<String, PersonFileImportStrategy> fileImportStrategyMap;

    public PersonFileImportStrategy getStrategy(String key) {
        PersonFileImportStrategy strategy = fileImportStrategyMap.get(key);
        if (strategy == null) {
            throw new ResourceNotFoundException("Unknown type: " + key);
        }
        return strategy;
    }
}
