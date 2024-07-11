package com.example.personmanagement.employee.position;

import com.example.personmanagement.employee.EmployeeRepository;
import com.example.personmanagement.employee.model.Employee;
import com.example.personmanagement.exception.JobOverlappingException;
import com.example.personmanagement.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JobServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private JobPositionRepository jobPositionRepository;

    @InjectMocks
    private JobService jobService;

    private CreatePositionCommand createPositionCommand;

    @BeforeEach
    void setUp() {
        createPositionCommand = new CreatePositionCommand(
                "Developer",
                LocalDate.of(2023, 7, 1),
                LocalDate.of(2024, 7, 1),
                5000
        );
    }
    @Test
    void shouldAddJobPositionSuccessfully() {
        Employee employee = new Employee();
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(jobPositionRepository.findByEmployeeIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                anyLong(), any(LocalDate.class), any(LocalDate.class)
        )).thenReturn(Collections.emptyList());

        JobPosition jobPosition = new JobPosition();
        jobPosition.setEmployee(employee);
        jobPosition.setPositionName(createPositionCommand.getPositionName());
        jobPosition.setStartDate(createPositionCommand.getStartDate());
        jobPosition.setEndDate(createPositionCommand.getEndDate());
        jobPosition.setSalary(createPositionCommand.getSalary());

        when(jobPositionRepository.save(any(JobPosition.class))).thenReturn(jobPosition);

        PositionDto result = jobService.addJobPosition(1L, createPositionCommand);

        assertNotNull(result);
        assertEquals("Developer", result.getPositionName());
        verify(employeeRepository).findById(1L);
        verify(jobPositionRepository).findByEmployeeIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                anyLong(), any(LocalDate.class), any(LocalDate.class)
        );
        verify(jobPositionRepository).save(any(JobPosition.class));
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenEmployeeNotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> jobService.addJobPosition(1L, createPositionCommand));

        verify(employeeRepository).findById(1L);
        verify(jobPositionRepository, never()).findByEmployeeIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                anyLong(), any(LocalDate.class), any(LocalDate.class)
        );
        verify(jobPositionRepository, never()).save(any(JobPosition.class));
    }

    @Test
    void shouldThrowJobOverlappingExceptionWhenOverlappingPositionsExist() {
        Employee employee = new Employee();
        JobPosition existingPosition = new JobPosition();
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(jobPositionRepository.findByEmployeeIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                anyLong(), any(LocalDate.class), any(LocalDate.class)
        )).thenReturn(Collections.singletonList(existingPosition));

        assertThrows(JobOverlappingException.class, () -> jobService.addJobPosition(1L, createPositionCommand));

        verify(employeeRepository).findById(1L);
        verify(jobPositionRepository).findByEmployeeIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                anyLong(), any(LocalDate.class), any(LocalDate.class)
        );
        verify(jobPositionRepository, never()).save(any(JobPosition.class));
    }
}