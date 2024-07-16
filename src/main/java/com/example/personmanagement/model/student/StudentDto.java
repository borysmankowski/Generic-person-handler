package com.example.personmanagement.model.student;

import com.example.personmanagement.model.person.PersonDto;
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
