package com.example.personmanagement.employee.model;

import com.example.personmanagement.employee.position.JobPosition;
import com.example.personmanagement.person.model.Person;
import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@DiscriminatorValue("EMPLOYEE")
@ToString(callSuper = true)
public class Employee extends Person {

    private LocalDate employmentStartDate;
    private String currentPosition;
    private double currentSalary;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "employee", orphanRemoval = true)
    private List<JobPosition> jobPositions;
    private int numberOfJobPositions = 0;

}