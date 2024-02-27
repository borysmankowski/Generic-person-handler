package com.example.personmanagement.employee.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class JobPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String positionName;
    private LocalDate startDate;
    private LocalDate endDate;
    private double salary;
    @Version
    private int version;

    public JobPosition(String positionName, LocalDate startDate, LocalDate endDate, double salary) {
        this.positionName = positionName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.salary = salary;
    }

    public boolean isOverlapping(JobPosition other) {
        return !(this.endDate.isBefore(other.startDate) || other.endDate.isBefore(this.startDate));

    }
}