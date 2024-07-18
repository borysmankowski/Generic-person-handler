package com.example.personmanagement.strategy;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.model.employee.CreateEmployeeCommand;
import com.example.personmanagement.model.employee.Employee;
import com.example.personmanagement.model.person.CreatePersonCommand;
import com.example.personmanagement.model.person.Person;
import org.springframework.stereotype.Component;

@Component("employeeCreationStrategy")
public class EmployeeCreationStrategy implements PersonCreationStrategy {
    @Override
    public Person create(CreatePersonCommand command) {
        if (!(command instanceof CreateEmployeeCommand employeeCommand)) {
            throw new InvalidStrategyTypeException("Invalid command type for EmployeeCreationStrategy");
        }

        return Employee.builder()
                .type(employeeCommand.getType())
                .name(employeeCommand.getName())
                .surname(employeeCommand.getSurname())
                .pesel(employeeCommand.getPesel())
                .height(employeeCommand.getHeight())
                .weight(employeeCommand.getWeight())
                .emailAddress(employeeCommand.getEmailAddress())
                .build();
    }

}