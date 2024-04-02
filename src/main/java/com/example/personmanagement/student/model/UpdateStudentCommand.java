package com.example.personmanagement.student.model;

import com.example.personmanagement.person.model.UpdatePersonCommand;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateStudentCommand extends UpdatePersonCommand {
    @NotBlank(message = "Name of the University cannot be empty")
    private String nameOfUniversity;
    private int yearOfStudies;
    @NotBlank(message = "The name of the course cannot be emtpty!")
    private String courseName;
    private double scholarship;
}
