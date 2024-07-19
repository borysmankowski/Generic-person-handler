package com.example.personmanagement.strategy.csv;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PensionerFileImportStrategyTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private PensionerFileImportStrategy pensionerFileImportStrategy;

    private List<String[]> validData;
    private List<String[]> invalidData;

    @BeforeEach
    void setUp() {
        validData = List.of(
                new String[]{"PENSIONER", "John", "Doe", "1234567890", "180.5", "75.0", "john.doe@example.com", "1500.0", "30"},
                new String[]{"PENSIONER", "Jane", "Doe", "0987654321", "165.2", "60.0", "jane.doe@example.com", "1600.0", "25"}
        );

        invalidData = List.of(
                new String[]{"INVALID", "John", "Doe", "1234567890", "180.5", "75.0", "john.doe@example.com", "1500.0", "30"},
                new String[]{"INVALID", "Jane", "Doe", "0987654321", "165.2", "60.0", "jane.doe@example.com", "1600.0", "25"}
        );
    }

    @Test
    public void testBulkInsert_withValidData() {
        pensionerFileImportStrategy.bulkInsert(validData, jdbcTemplate);

        verify(jdbcTemplate).batchUpdate(anyString(), anyList());
    }

    @Test
    public void testBulkInsert_withInvalidData_shouldThrowException() {
        assertThrows(InvalidStrategyTypeException.class, () -> {
            pensionerFileImportStrategy.bulkInsert(invalidData, jdbcTemplate);
        });
    }

    @Test
    public void testBulkInsert_whenJdbcTemplateThrowsException() {
        doThrow(RuntimeException.class).when(jdbcTemplate).batchUpdate(anyString(), anyList());

        assertThrows(RuntimeException.class, () -> {
            pensionerFileImportStrategy.bulkInsert(validData, jdbcTemplate);
        });
    }
}