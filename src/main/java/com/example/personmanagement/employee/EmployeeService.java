package com.example.personmanagement.employee;

import com.example.personmanagement.employee.model.AddJobPositionCommand;
import com.example.personmanagement.employee.model.Employee;
import com.example.personmanagement.employee.model.JobPosition;
import com.example.personmanagement.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public void addJobPosition(Long employeeId, AddJobPositionCommand command) {
        JobPosition jobPosition = new JobPosition(
                command.positionName(),
                command.startDate(),
                command.endDate(),
                command.salary()
        );

        Employee employee = employeeRepository.findEmployeeWithLock(employeeId)
                .filter(Employee.class::isInstance)
                .map(Employee.class::cast)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));

        LocalDate currentDate = LocalDate.now();

        if (currentDate.isAfter(jobPosition.getStartDate()) || currentDate.isEqual(jobPosition.getStartDate())) {
            employee.setCurrentPosition(jobPosition.getPositionName());
            employee.setCurrentSalary(jobPosition.getSalary());
            employee.setEmploymentStartDate(jobPosition.getStartDate());
        }

        JobPosition currentJobPosition = employee.findCurrentPosition(currentDate);
        if (currentJobPosition != null) {
            employee.setCurrentPosition(currentJobPosition.getPositionName());
        }

        employee.addJobPosition(jobPosition);
        employeeRepository.save(employee);
    }

}

