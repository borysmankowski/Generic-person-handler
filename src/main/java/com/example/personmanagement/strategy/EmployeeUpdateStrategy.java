package com.example.personmanagement.strategy;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.exception.ResourceNotFoundException;
import com.example.personmanagement.model.employee.Employee;
import com.example.personmanagement.model.person.Person;
import com.example.personmanagement.model.person.UpdatePersonCommand;
import com.example.personmanagement.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("employeeUpdateStrategy")
@RequiredArgsConstructor
public class EmployeeUpdateStrategy implements PersonUpdateStrategy {

    private final PersonRepository personRepository;
    final String EXPECTED_TYPE = "EMPLOYEE";

    @Override
    public Person update(long existingPerson, UpdatePersonCommand command) {

        Employee existingEmployee = personRepository.findByIdWithJobs(existingPerson)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + existingPerson));

        if (command.getType() == null || !EXPECTED_TYPE.equals(command.getType())) {
            throw new InvalidStrategyTypeException("Invalid type for EmployeeUpdateStrategy: expected " + EXPECTED_TYPE + " but got " + command.getType());
        }

        return Employee.builder()
                .id(existingEmployee.getId())
                .type(existingEmployee.getType())
                .name(command.getName() != null ? command.getName() : existingEmployee.getName())
                .surname(command.getSurname() != null ? command.getSurname() : existingEmployee.getSurname())
                .pesel(command.getPesel() != null ? command.getPesel() : existingEmployee.getPesel())
                .height(command.getHeight() != 0.0 ? command.getHeight() : existingEmployee.getHeight())
                .weight(command.getWeight() != 0.0 ? command.getWeight() : existingEmployee.getWeight())
                .emailAddress(command.getEmailAddress() != null ? command.getEmailAddress() : existingEmployee.getEmailAddress())
                .jobPositions(existingEmployee.getJobPositions())
                .version(command.getVersion())
                .build();
    }
}