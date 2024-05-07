package com.example.personmanagement.employee;

import com.example.personmanagement.employee.model.AddJobPositionCommand;
import com.example.personmanagement.employee.model.JobPositionResponseBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;


    @PostMapping("/{employeeId}/positions")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<JobPositionResponseBody> addJobPositionToPerson(
            @PathVariable Long employeeId,
            @RequestBody AddJobPositionCommand command) {
        employeeService.addJobPosition(employeeId, command);

        JobPositionResponseBody responseBody = new JobPositionResponseBody("Job position added successfully", employeeId);

        return ResponseEntity.ok(responseBody);
    }

}