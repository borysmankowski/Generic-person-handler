package com.example.personmanagement.employee.model;

import java.time.LocalDate;

public record AddJobPositionCommand(String positionName, LocalDate startDate, LocalDate endDate, double salary) {
}
