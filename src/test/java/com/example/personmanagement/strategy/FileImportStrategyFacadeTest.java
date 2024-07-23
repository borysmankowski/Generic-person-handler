package com.example.personmanagement.strategy;

import com.example.personmanagement.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileImportStrategyFacadeTest {
    @Mock
    private PersonFileImportStrategy mockStrategy;

    private FileImportStrategyFacade strategyFacade;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Map<String, PersonFileImportStrategy> strategyMap = new HashMap<>();
        strategyMap.put("EMPLOYEE", mockStrategy);
        strategyMap.put("PENSIONER", mockStrategy);
        strategyMap.put("STUDENT", mockStrategy);

        strategyFacade = new FileImportStrategyFacade(strategyMap);
    }

    @Test
    public void testGetStrategy_ValidKeyEMPLOYEE() {
        String key = "EMPLOYEE";
        PersonFileImportStrategy strategy = strategyFacade.getStrategy(key);
        assertEquals(mockStrategy, strategy);
    }

    @Test
    public void testGetStrategy_ValidKeySTUDENT() {
        String key = "STUDENT";
        PersonFileImportStrategy strategy = strategyFacade.getStrategy(key);
        assertEquals(mockStrategy, strategy);
    }

    @Test
    public void testGetStrategy_ValidKeyPENSIONER() {
        String key = "PENSIONER";
        PersonFileImportStrategy strategy = strategyFacade.getStrategy(key);
        assertEquals(mockStrategy, strategy);
    }

    @Test
    public void testGetStrategy_InvalidKey() {
        String key = "invalid";
        assertThrows(ResourceNotFoundException.class, () -> strategyFacade.getStrategy(key));
    }
}