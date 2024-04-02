package com.example.personmanagement.employee.model;

import com.example.personmanagement.person.model.UpdatePersonCommand;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateEmployeeCommand extends UpdatePersonCommand {

    private LocalDate employmentStartDate;

    private String currentPosition;
    private double currentSalary;
}
