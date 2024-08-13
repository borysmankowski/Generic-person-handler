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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {

    private final PersonRepository personRepository;
    private final JobPositionRepository jobPositionRepository;

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public PositionDto addJobPosition(Long employeeId, CreatePositionCommand command) {

        Employee employee = (Employee) personRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));

        List<JobPosition> overlappingPositions = jobPositionRepository.findByEmployeeIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                employeeId, command.getEndDate(), command.getStartDate());

        if (!overlappingPositions.isEmpty()) {
            throw new JobOverlappingException("The position dates overlap with existing position dates.");
        }

        JobPosition newPosition = PositionMapper.fromCreateCommand(command);
        newPosition.setEmployee(employee);
        return PositionMapper.toDto(jobPositionRepository.save(newPosition));
    }
}