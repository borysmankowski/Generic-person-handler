package com.example.personmanagement.student.model;

import com.example.personmanagement.person.model.CreatePersonCommand;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CreateStudentCommand.class, name = "STUDENT"),
})
@Data
@ToString(callSuper = true)
public class CreateStudentCommand extends CreatePersonCommand {

    @NotBlank(message = "Name of the University cannot be empty")
    private String nameOfUniversity;
    private int yearOfStudies;
    @NotBlank(message = "The name of the course cannot be emtpty!")
    private String courseName;
    private double scholarship;
}
