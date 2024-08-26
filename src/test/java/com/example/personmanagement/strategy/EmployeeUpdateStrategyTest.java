package com.example.personmanagement.strategy;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.model.employee.Employee;
import com.example.personmanagement.model.person.Person;
import com.example.personmanagement.model.person.UpdatePersonCommand;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmployeeUpdateStrategyTest {
    private final EmployeeUpdateStrategy strategy = new EmployeeUpdateStrategy();

    @Test
    void update_withValidUpdateEmployeeCommand_shouldUpdateEmployee() {

        Employee existingEmployee = new Employee();
        existingEmployee.setName("John");
        existingEmployee.setSurname("Doe");
        existingEmployee.setPesel("1234567890");
        existingEmployee.setHeight(180);
        existingEmployee.setWeight(75);
        existingEmployee.setEmailAddress("john.doe@example.com");

        UpdatePersonCommand command = new UpdatePersonCommand();
        command.setType("EMPLOYEE");
        command.setName("Johnny");
        command.setHeight(185);

        Employee updatedEmployee = (Employee) strategy.update(existingEmployee, command);

        assertNotNull(updatedEmployee);
        assertEquals("Johnny", updatedEmployee.getName());
        assertEquals("Doe", updatedEmployee.getSurname());
        assertEquals("1234567890", updatedEmployee.getPesel());
        assertEquals(185, updatedEmployee.getHeight());
        assertEquals(75, updatedEmployee.getWeight());
        assertEquals("john.doe@example.com", updatedEmployee.getEmailAddress());
    }

    @Test
    void update_withInvalidCommandType_shouldThrowInvalidStrategyTypeException() {

        Person existingPerson = new Person();
        UpdatePersonCommand invalidCommand = new UpdatePersonCommand() {

        };
        assertThrows(InvalidStrategyTypeException.class, () -> strategy.update(existingPerson, invalidCommand));
    }

    @Test
    void update_withInvalidExistingPersonType_shouldThrowIllegalArgumentException() {

        Person existingPerson = new Person() {
        };
        UpdatePersonCommand validCommand = new UpdatePersonCommand();
        assertThrows(InvalidStrategyTypeException.class, () -> strategy.update(existingPerson, validCommand));
    }
}