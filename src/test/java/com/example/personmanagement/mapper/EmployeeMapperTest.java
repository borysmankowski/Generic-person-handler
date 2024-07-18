package com.example.personmanagement.mapper;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.model.employee.Employee;
import com.example.personmanagement.model.employee.EmployeeDto;
import com.example.personmanagement.model.person.Person;
import com.example.personmanagement.model.position.JobPosition;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmployeeMapperTest {

    private final EmployeeMapper employeeMapper = new EmployeeMapper();

    @Test
    void supports() {
        assertTrue(employeeMapper.supports("EMPLOYEE"));
        assertFalse(employeeMapper.supports("MANAGER"));
    }

    @Test
    void toDto() {
        JobPosition jobPosition1 = JobPosition.builder()
                .positionName("Developer")
                .startDate(LocalDate.of(2020, 1, 1))
                .salary(3000)
                .build();

        Set<JobPosition> jobPositions = new HashSet<>();
        jobPositions.add(jobPosition1);

        Employee employee = Employee.builder()
                .id(1L)
                .name("John")
                .surname("Doe")
                .pesel("1234567890")
                .height(180)
                .weight(75)
                .emailAddress("john.doe@example.com")
                .jobPositions(jobPositions)
                .build();

        EmployeeDto employeeDto = (EmployeeDto) employeeMapper.toDto(employee);

        assertEquals(1L, employeeDto.getId());
        assertEquals("John", employeeDto.getName());
        assertEquals("Doe", employeeDto.getSurname());
        assertEquals("1234567890", employeeDto.getPesel());
        assertEquals(180, employeeDto.getHeight());
        assertEquals(75, employeeDto.getWeight());
        assertEquals("john.doe@example.com", employeeDto.getEmailAddress());
        assertEquals("Developer", employeeDto.getCurrentJobPosition());
    }

    @Test
    void toDto_withInvalidType() {
        Person invalidPerson = new Person() {
        };
        assertThrows(InvalidStrategyTypeException.class, () -> employeeMapper.toDto(invalidPerson));
    }
}