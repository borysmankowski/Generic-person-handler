package com.example.personmanagement.model.student;

import com.example.personmanagement.model.person.UpdatePersonCommand;
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
