package com.example.personmanagement.employee;

import com.example.personmanagement.employee.model.CreateEmployeeCommand;
import com.example.personmanagement.employee.model.Employee;
import com.example.personmanagement.employee.model.UpdateEmployeeCommand;
import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.person.PersonCreationStrategy;
import com.example.personmanagement.person.model.CreatePersonCommand;
import com.example.personmanagement.person.model.Person;
import com.example.personmanagement.person.model.UpdatePersonCommand;
import org.springframework.stereotype.Component;

@Component("EMPLOYEE")
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
                .employmentStartDate(employeeCommand.getEmploymentStartDate())
                .currentPosition(employeeCommand.getCurrentPosition())
                .currentSalary(employeeCommand.getCurrentSalary())
                .build();
    }

    @Override
    public Person update(UpdatePersonCommand command) {
        if (!(command instanceof UpdateEmployeeCommand employeeCommand)) {
            throw new InvalidStrategyTypeException("Invalid command type for EmployeeUpdateStrategy");
        }

        return Employee.builder()
                .type(employeeCommand.getType())
                .name(employeeCommand.getName())
                .surname(employeeCommand.getSurname())
                .pesel(employeeCommand.getPesel())
                .height(employeeCommand.getHeight())
                .weight(employeeCommand.getWeight())
                .emailAddress(employeeCommand.getEmailAddress())
                .employmentStartDate(employeeCommand.getEmploymentStartDate())
                .currentPosition(employeeCommand.getCurrentPosition())
                .currentSalary(employeeCommand.getCurrentSalary())
                .build();
    }

}

