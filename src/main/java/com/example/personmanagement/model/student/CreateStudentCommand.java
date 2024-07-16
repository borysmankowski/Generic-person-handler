package com.example.personmanagement.model.student;

import com.example.personmanagement.model.person.CreatePersonCommand;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CreateStudentCommand extends CreatePersonCommand {

    @NotBlank(message = "Name of the University cannot be empty")
    private String nameOfUniversity;
    private int yearOfStudies;
    @NotBlank(message = "The name of the course cannot be emtpty!")
    private String courseName;
    private double scholarship;
}