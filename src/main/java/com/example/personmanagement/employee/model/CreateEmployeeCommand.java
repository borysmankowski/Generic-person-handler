package com.example.personmanagement.employee.model;

import com.example.personmanagement.person.model.CreatePersonCommand;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateEmployeeCommand extends CreatePersonCommand {

    private LocalDate employmentStartDate;

    private String currentPosition;
    private double currentSalary;

}
