package com.example.personmanagement.employee.model;

import com.example.personmanagement.employee.position.JobPosition;
import com.example.personmanagement.exception.JobOverlappingException;
import com.example.personmanagement.person.model.Person;
import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
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

    public JobPosition findCurrentPosition(LocalDate currentDate) {
        return jobPositions.stream()
                .filter(jobPosition -> jobPosition.getStartDate().isAfter(currentDate))
                .max(Comparator.comparing(JobPosition::getStartDate))
                .orElse(null);
    }

    public void addJobPosition(JobPosition jobPosition) {
        if (jobPositions == null) {
            jobPositions = new ArrayList<>();
        }

        for (JobPosition existingPosition : jobPositions) {
            if ((jobPosition.getStartDate().isBefore(existingPosition.getEndDate()) || jobPosition.getStartDate().isEqual(existingPosition.getEndDate())) &&
                    (jobPosition.getEndDate().isAfter(existingPosition.getStartDate()) || jobPosition.getEndDate().isEqual(existingPosition.getStartDate()))) {
                throw new JobOverlappingException("New job position overlaps with an existing position");
            }
        }
        jobPositions.add(jobPosition);
        jobPosition.setEmployee(this);
        numberOfJobPositions++;
    }
}