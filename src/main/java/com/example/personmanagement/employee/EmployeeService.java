package com.example.personmanagement.employee;

import com.example.personmanagement.employee.model.Employee;
import com.example.personmanagement.employee.position.AddJobPositionCommand;
import com.example.personmanagement.employee.position.JobPosition;
import com.example.personmanagement.exception.JobOverlappingException;
import com.example.personmanagement.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    private final Clock clock;

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public void addJobPosition(Long employeeId, AddJobPositionCommand command) {
        JobPosition jobPosition = new JobPosition(
                command.positionName(),
                command.startDate(),
                command.endDate(),
                command.salary()
        );

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));

        LocalDate currentDate = LocalDate.now(clock);

        for (JobPosition existingPosition : employee.getJobPositions()) {
            LocalDate existingStartDate = existingPosition.getStartDate();
            LocalDate existingEndDate = existingPosition.getEndDate();

            if ((jobPosition.getStartDate().isBefore(existingEndDate) || jobPosition.getStartDate().isEqual(existingEndDate)) &&
                    (jobPosition.getEndDate().isAfter(existingStartDate) || jobPosition.getEndDate().isEqual(existingStartDate))) {
                throw new JobOverlappingException("New job position overlaps with an existing position");
            }
        }

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