package com.example.personmanagement.service;

import com.example.personmanagement.exception.JobOverlappingException;
import com.example.personmanagement.exception.ResourceNotFoundException;
import com.example.personmanagement.mapper.PositionMapper;
import com.example.personmanagement.model.employee.Employee;
import com.example.personmanagement.model.position.CreatePositionCommand;
import com.example.personmanagement.model.position.JobPosition;
import com.example.personmanagement.model.position.PositionDto;
import com.example.personmanagement.repository.JobPositionRepository;
import com.example.personmanagement.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ConcurrentModificationException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {

    private final PersonRepository personRepository;
    private final JobPositionRepository jobPositionRepository;
    private static final LocalDate FAR_FUTURE = LocalDate.of(9999, 12, 31);

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public PositionDto addJobPosition(Long employeeId, CreatePositionCommand command) {

        Employee employee = personRepository.findByIdWithJobs(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));

        validateJobPositionDates(employeeId, command);

        try {
            JobPosition newPosition = PositionMapper.fromCreateCommand(command);
            newPosition.setEmployee(employee);
            return PositionMapper.toDto(jobPositionRepository.save(newPosition));
        } catch (OptimisticLockingFailureException e) {
            throw new ConcurrentModificationException("The job position was modified by another transaction. Please try again.");
        }
    }

    private void validateJobPositionDates(Long employeeId, CreatePositionCommand command) {
        LocalDate startDate = command.getStartDate();
        LocalDate effectiveEndDate = getLocalDate(command, startDate);

        List<JobPosition> overlappingPositions =
                jobPositionRepository.findByEmployeeIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        employeeId, effectiveEndDate, startDate);

        if (!overlappingPositions.isEmpty()) {
            throw new JobOverlappingException("The position dates overlap with existing position dates.");
        }
    }

    private static LocalDate getLocalDate(CreatePositionCommand command, LocalDate startDate) {
        LocalDate endDate   = command.getEndDate();

        if (startDate == null) {
            throw new JobOverlappingException("The position start date is required.");
        }
        if (endDate != null && endDate.isBefore(startDate)) {
            throw new JobOverlappingException("The position end date is before the start date.");
        }
        return (endDate != null) ? endDate : FAR_FUTURE;
    }
}