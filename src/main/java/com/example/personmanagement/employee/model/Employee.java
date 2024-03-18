package com.example.personmanagement.employee.model;

import com.example.personmanagement.exception.JobOverlappingException;
import com.example.personmanagement.person.model.Person;
import jakarta.persistence.CascadeType;
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
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@ToString(callSuper = true)
public class Employee extends Person {

    private LocalDate employmentStartDate;
    private String currentPosition;
    private double currentSalary;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<JobPosition> jobPositions;
    private int numberOfJobPositions = 0;

    public JobPosition findCurrentPosition(LocalDate currentDate) {
        return jobPositions.stream()
                .filter(it -> currentDate.isAfter(it.getStartDate()) && currentDate.isBefore(it.getEndDate()))
                .findFirst().orElse(null);
    }

    public void addJobPosition(JobPosition jobPosition) {
        if (jobPositions == null) {
            jobPositions = new HashSet<>();
        }
        for (JobPosition existingPosition : jobPositions) {
            if (existingPosition.isOverlapping(jobPosition)) {
                throw new JobOverlappingException("New job position overlaps with existing position");
            }
        }
        jobPositions.add(jobPosition);
        numberOfJobPositions++;
    }
}