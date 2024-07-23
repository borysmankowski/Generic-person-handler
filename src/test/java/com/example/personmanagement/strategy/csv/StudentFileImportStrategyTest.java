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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StudentFileImportStrategyTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private StudentFileImportStrategy studentFileImportStrategy;

    private List<String[]> validData;
    private List<String[]> invalidData;

    @BeforeEach
    void setUp() {
        validData = List.of(
                new String[]{"STUDENT", "John", "Doe", "1234567890", "180.5", "75.0", "john.doe@example.com", "University of Example", "2", "Computer Science", "500.0"},
                new String[]{"STUDENT", "Jane", "Doe", "0987654321", "165.2", "60.0", "jane.doe@example.com", "Example University", "1", "Mathematics", "600.0"}
        );

        invalidData = List.of(
                new String[]{"INVALID", "John", "Doe", "1234567890", "180.5", "75.0", "john.doe@example.com", "University of Example", "2", "Computer Science", "500.0"},
                new String[]{"INVALID", "Jane", "Doe", "0987654321", "165.2", "60.0", "jane.doe@example.com", "Example University", "1", "Mathematics", "600.0"}
        );
    }

    @Test
    public void testBulkInsert_withValidData() {
        studentFileImportStrategy.bulkInsert(validData, jdbcTemplate);

        verify(jdbcTemplate).batchUpdate(anyString(), anyList());
    }

    @Test
    public void testBulkInsert_withInvalidData_shouldThrowException() {
        assertThrows(InvalidStrategyTypeException.class, () -> {
            studentFileImportStrategy.bulkInsert(invalidData, jdbcTemplate);
        });
    }

    @Test
    public void testBulkInsert_whenJdbcTemplateThrowsException() {
        doThrow(RuntimeException.class).when(jdbcTemplate).batchUpdate(anyString(), anyList());

        assertThrows(RuntimeException.class, () -> {
            studentFileImportStrategy.bulkInsert(validData, jdbcTemplate);
        });
    }
}