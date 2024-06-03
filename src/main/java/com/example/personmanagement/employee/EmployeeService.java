package com.example.personmanagement.employee;

import com.example.personmanagement.employee.model.Employee;
import com.example.personmanagement.employee.position.CreatePositionCommand;
import com.example.personmanagement.employee.position.JobPosition;
import com.example.personmanagement.employee.position.JobPositionRepository;
import com.example.personmanagement.employee.position.PositionDto;
import com.example.personmanagement.exception.ResourceNotFoundException;
import com.example.personmanagement.mapper.PositionMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    private final JobPositionRepository jobPositionRepository;

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public PositionDto addJobPosition(Long employeeId, CreatePositionCommand command) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));

        List<JobPosition> overlappingPositions = jobPositionRepository.findByEmployeeIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                employeeId, command.getEndDate(), command.getStartDate());

        if (!overlappingPositions.isEmpty()) {
            throw new ResourceNotFoundException("The position dates overlap with existing position dates.");
        }

        JobPosition newPosition = PositionMapper.fromCreateCommand(command);
        newPosition.setEmployee(employee);
        return PositionMapper.toDto(jobPositionRepository.save(newPosition));

    }
}