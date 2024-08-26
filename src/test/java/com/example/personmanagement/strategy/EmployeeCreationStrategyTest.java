package com.example.personmanagement.strategy;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.model.employee.Employee;
import com.example.personmanagement.model.person.CreatePersonCommand;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmployeeCreationStrategyTest {

    private final EmployeeCreationStrategy strategy = new EmployeeCreationStrategy();

    @Test
    void create_withValidCreateEmployeeCommand_shouldReturnEmployee() {

        CreatePersonCommand command = new CreatePersonCommand();
        command.setType("EMPLOYEE");
        command.setName("John");
        command.setSurname("Doe");
        command.setPesel("1234567890");
        command.setHeight(180);
        command.setWeight(75);
        command.setEmailAddress("john.doe@example.com");

        Employee result = (Employee) strategy.create(command);

        assertNotNull(result);
        assertEquals("EMPLOYEE", result.getType());
        assertEquals("John", result.getName());
        assertEquals("Doe", result.getSurname());
        assertEquals("1234567890", result.getPesel());
        assertEquals(180, result.getHeight());
        assertEquals(75, result.getWeight());
        assertEquals("john.doe@example.com", result.getEmailAddress());
    }

    @Test
    void create_withInvalidCommandType_shouldThrowInvalidStrategyTypeException() {
        CreatePersonCommand invalidCommand = new CreatePersonCommand() {
        };
        assertThrows(InvalidStrategyTypeException.class, () -> strategy.create(invalidCommand));
    }
}