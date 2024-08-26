package com.example.personmanagement.strategy;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.model.employee.Employee;
import com.example.personmanagement.model.person.CreatePersonCommand;
import com.example.personmanagement.model.person.Person;
import org.springframework.stereotype.Component;

@Component("employeeCreationStrategy")
public class EmployeeCreationStrategy implements PersonCreationStrategy {

    private static final String EXPECTED_TYPE = "EMPLOYEE";

    @Override
    public Person create(CreatePersonCommand command) {

        if (!EXPECTED_TYPE.equals(command.getType())) {
            throw new InvalidStrategyTypeException("Invalid type for EmployeeCreationStrategy: expected " + EXPECTED_TYPE + " but got " + command.getType());
        }

        return Employee.builder()
                .type(command.getType())
                .name(command.getName())
                .surname(command.getSurname())
                .pesel(command.getPesel())
                .height(command.getHeight())
                .weight(command.getWeight())
                .emailAddress(command.getEmailAddress())
                .build();
    }
}