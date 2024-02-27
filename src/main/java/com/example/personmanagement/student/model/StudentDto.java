package com.example.personmanagement.student.model;

import com.example.personmanagement.person.model.PersonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class StudentDto extends PersonDto {

    private String nameOfUniversity;
    private int yearOfStudies;
    private String courseName;
    private double scholarship;
}
