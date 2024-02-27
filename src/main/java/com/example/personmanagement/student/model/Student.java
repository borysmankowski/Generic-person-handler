package com.example.personmanagement.student.model;

import com.example.personmanagement.person.model.Person;
import jakarta.persistence.Entity;
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
@Entity
@ToString(callSuper = true)
public class Student extends Person {

    private String nameOfUniversity;
    private int yearOfStudies;
    private String courseName;
    private double scholarship;
}
