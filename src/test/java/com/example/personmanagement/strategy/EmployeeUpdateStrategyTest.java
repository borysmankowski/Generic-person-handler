package com.example.personmanagement.strategy;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.exception.ResourceNotFoundException;
import com.example.personmanagement.model.employee.Employee;
import com.example.personmanagement.model.person.Person;
import com.example.personmanagement.model.person.UpdatePersonCommand;
import com.example.personmanagement.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmployeeUpdateStrategyTest {

    @InjectMocks
    private EmployeeUpdateStrategy strategy;

    @Mock
    private PersonRepository personRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void update_withValidUpdateEmployeeCommand_shouldUpdateEmployee() {
        long existingPersonId = 1L;

        Employee existingEmployee = new Employee();
        existingEmployee.setId(existingPersonId);
        existingEmployee.setName("John");
        existingEmployee.setSurname("Doe");
        existingEmployee.setPesel("1234567890");
        existingEmployee.setHeight(180);
        existingEmployee.setWeight(75);
        existingEmployee.setEmailAddress("john.doe@example.com");

        when(personRepository.findByIdWithJobs(existingPersonId)).thenReturn(Optional.of(existingEmployee));

        UpdatePersonCommand command = new UpdatePersonCommand();
        command.setType("EMPLOYEE");
        command.setName("Johnny");
        command.setHeight(185);

        Employee updatedEmployee = (Employee) strategy.update(existingPersonId, command);

        assertNotNull(updatedEmployee);
        assertEquals("Johnny", updatedEmployee.getName());
        assertEquals("Doe", updatedEmployee.getSurname());
        assertEquals("1234567890", updatedEmployee.getPesel());
        assertEquals(185, updatedEmployee.getHeight());
        assertEquals(75, updatedEmployee.getWeight());
        assertEquals("john.doe@example.com", updatedEmployee.getEmailAddress());

        verify(personRepository, times(1)).findByIdWithJobs(existingPersonId);
    }

    @Test
    void update_withNonExistentEmployee_shouldThrowResourceNotFoundException() {
        long nonExistentPersonId = 99L;

        when(personRepository.findByIdWithJobs(nonExistentPersonId)).thenReturn(Optional.empty());

        UpdatePersonCommand validCommand = new UpdatePersonCommand();
        validCommand.setType("EMPLOYEE");

        assertThrows(ResourceNotFoundException.class, () -> strategy.update(nonExistentPersonId, validCommand));

        verify(personRepository, times(1)).findByIdWithJobs(nonExistentPersonId);
    }

    @Test
    void update_withInvalidExistingPersonType_shouldThrowInvalidStrategyTypeException() {
        long existingPersonId = 1L;

        Employee existingEmployee = new Employee();
        existingEmployee.setId(existingPersonId);

        when(personRepository.findByIdWithJobs(existingPersonId)).thenReturn(Optional.of(existingEmployee));

        UpdatePersonCommand validCommand = new UpdatePersonCommand();
        validCommand.setType("INVALID");

        assertThrows(InvalidStrategyTypeException.class, () -> strategy.update(existingPersonId, validCommand));

        verify(personRepository, times(1)).findByIdWithJobs(existingPersonId);
    }
}