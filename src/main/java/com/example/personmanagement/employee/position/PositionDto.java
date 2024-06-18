package com.example.personmanagement.employee.position;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class PositionDto {

    private Long id;

    private String positionName;
    private LocalDate startDate;
    private LocalDate endDate;
    private double salary;
    private long employeeId;

}
