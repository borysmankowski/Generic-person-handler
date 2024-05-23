package com.example.personmanagement.employee.position;

import com.example.personmanagement.employee.model.Employee;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    public JobPosition(String positionName, LocalDate startDate, LocalDate endDate, double salary) {
        this.positionName = positionName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.salary = salary;
    }

}