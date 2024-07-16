package com.example.personmanagement.controller;

import com.example.personmanagement.model.position.CreatePositionCommand;
import com.example.personmanagement.service.JobService;
import com.example.personmanagement.model.position.PositionDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    private final JobService jobService;

    @PostMapping("/{employeeId}/positions")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<PositionDto> addJobPositionToPerson(@PathVariable Long employeeId, @RequestBody @Valid CreatePositionCommand command) {
        return ResponseEntity.status(HttpStatus.OK).body(jobService.addJobPosition(employeeId, command));
    }
}