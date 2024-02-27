package com.example.personmanagement.employee;

import com.example.personmanagement.employee.model.AddJobPositionCommand;
import com.example.personmanagement.employee.model.JobPosition;
import com.example.personmanagement.employee.model.JobPositionResponseBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;


    @PostMapping("/{personId}/positions")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<JobPositionResponseBody> addJobPositionToPerson(
            @PathVariable Long personId,
            @RequestBody AddJobPositionCommand command) {
        employeeService.addJobPosition(personId, command);

        JobPositionResponseBody responseBody = new JobPositionResponseBody("Job position added successfully",personId);

        return ResponseEntity.ok(responseBody);
    }

}
