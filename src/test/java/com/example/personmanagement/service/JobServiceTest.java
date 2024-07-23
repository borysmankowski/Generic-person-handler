package com.example.personmanagement.service;

import com.example.personmanagement.exception.JobOverlappingException;
import com.example.personmanagement.exception.ResourceNotFoundException;
import com.example.personmanagement.model.employee.Employee;
import com.example.personmanagement.model.position.CreatePositionCommand;
import com.example.personmanagement.model.position.JobPosition;
import com.example.personmanagement.model.position.PositionDto;
import com.example.personmanagement.repository.JobPositionRepository;
import com.example.personmanagement.repository.PersonRepository;
import com.example.personmanagement.service.JobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JobServiceTest {

    @Mock
    private PersonRepository personRepository;

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
        when(personRepository.findById(1L)).thenReturn(Optional.of(employee));
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
        verify(personRepository).findById(1L);
        verify(jobPositionRepository).findByEmployeeIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                anyLong(), any(LocalDate.class), any(LocalDate.class)
        );
        verify(jobPositionRepository).save(any(JobPosition.class));
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenEmployeeNotFound() {
        when(personRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> jobService.addJobPosition(1L, createPositionCommand));

        verify(personRepository).findById(1L);
        verify(jobPositionRepository, never()).findByEmployeeIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                anyLong(), any(LocalDate.class), any(LocalDate.class)
        );
        verify(jobPositionRepository, never()).save(any(JobPosition.class));
    }

    @Test
    void shouldThrowJobOverlappingExceptionWhenOverlappingPositionsExist() {
        Employee employee = new Employee();
        JobPosition existingPosition = new JobPosition();
        when(personRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(jobPositionRepository.findByEmployeeIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                anyLong(), any(LocalDate.class), any(LocalDate.class)
        )).thenReturn(Collections.singletonList(existingPosition));

        assertThrows(JobOverlappingException.class, () -> jobService.addJobPosition(1L, createPositionCommand));

        verify(personRepository).findById(1L);
        verify(jobPositionRepository).findByEmployeeIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                anyLong(), any(LocalDate.class), any(LocalDate.class)
        );
        verify(jobPositionRepository, never()).save(any(JobPosition.class));
    }
}