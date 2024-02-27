package com.example.personmanagement.employee.model;

import com.example.personmanagement.person.model.CreatePersonCommand;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CreateEmployeeCommand.class, name = "employee"),
})
@Data
public class CreateEmployeeCommand extends CreatePersonCommand {

    private LocalDate employmentStartDate;

    private String currentPosition;
    private double currentSalary;

}
