package com.example.personmanagement.mapper;

import com.example.personmanagement.model.employee.Employee;
import com.example.personmanagement.model.position.CreatePositionCommand;
import com.example.personmanagement.model.position.JobPosition;
import com.example.personmanagement.model.position.PositionDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class PositionMapperTest {

    @Test
    void toDto() {
        Employee employee = Employee.builder().id(1L).build();
        JobPosition jobPosition = JobPosition.builder()
                .id(1L)
                .positionName("Developer")
                .salary(3000)
                .startDate(LocalDate.of(2021, 1, 1))
                .endDate(LocalDate.of(2022, 1, 1))
                .employee(employee)
                .build();

        PositionDto dto = PositionMapper.toDto(jobPosition);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Developer", dto.getPositionName());
        assertEquals(3000, dto.getSalary());
        assertEquals(LocalDate.of(2021, 1, 1), dto.getStartDate());
        assertEquals(LocalDate.of(2022, 1, 1), dto.getEndDate());
        assertEquals(1L, dto.getEmployeeId());
    }

    @Test
    void toDto_withNullJobPosition() {
        PositionDto dto = PositionMapper.toDto(null);

        assertNull(dto);
    }

    @Test
    void fromCreateCommand() {
        CreatePositionCommand command = new CreatePositionCommand();
                command.setPositionName("Manager");
                command.setSalary(3000);
                command.setStartDate(LocalDate.of(2021, 6, 1));
                command.setEndDate(LocalDate.of(2022, 6, 1));

        JobPosition jobPosition = PositionMapper.fromCreateCommand(command);

        assertNotNull(jobPosition);
        assertEquals("Manager", jobPosition.getPositionName());
        assertEquals(3000, jobPosition.getSalary());
        assertEquals(LocalDate.of(2021, 6, 1), jobPosition.getStartDate());
        assertEquals(LocalDate.of(2022, 6, 1), jobPosition.getEndDate());
    }

    @Test
    void fromCreateCommand_withNullValues() {
        CreatePositionCommand command = new CreatePositionCommand();

        JobPosition jobPosition = PositionMapper.fromCreateCommand(command);

        assertNotNull(jobPosition);
        assertNull(jobPosition.getPositionName());
        assertNull(null);
        assertNull(jobPosition.getStartDate());
        assertNull(jobPosition.getEndDate());
    }
}