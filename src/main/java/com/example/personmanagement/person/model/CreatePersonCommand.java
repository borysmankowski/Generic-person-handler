package com.example.personmanagement.person.model;

import com.example.personmanagement.employee.model.CreateEmployeeCommand;
import com.example.personmanagement.pensioner.model.CreatePensionerCommand;
import com.example.personmanagement.student.model.CreateStudentCommand;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.pl.PESEL;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CreatePensionerCommand.class, name = "PENSIONER"),
        @JsonSubTypes.Type(value = CreateStudentCommand.class, name = "STUDENT"),
        @JsonSubTypes.Type(value = CreateEmployeeCommand.class, name = "EMPLOYEE"),
})
@Data
public class CreatePersonCommand {

    private String type;
    @NotBlank(message = "Name cannot be blank")
    private String name;
    @NotBlank(message = "Surname cannot be blank")
    private String surname;
    @PESEL
    private String pesel;
    private double height;
    private double weight;
    @Email
    private String emailAddress;
}
