package com.example.personmanagement.strategy;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.model.employee.Employee;
import com.example.personmanagement.model.employee.UpdateEmployeeCommand;
import com.example.personmanagement.model.person.Person;
import com.example.personmanagement.model.person.UpdatePersonCommand;
import org.springframework.stereotype.Component;

@Component("employeeUpdateStrategy")
public class EmployeeUpdateStrategy implements PersonUpdateStrategy {
    @Override
    public Person update(Person existingPerson, UpdatePersonCommand command) {
        if (!(command instanceof UpdateEmployeeCommand employeeCommand)) {
            throw new InvalidStrategyTypeException("Invalid command type for EmployeeUpdateStrategy");
        }
        if (existingPerson instanceof Employee existingEmployee) {

            return Employee.builder()
                    .id(existingEmployee.getId())
                    .type(employeeCommand.getType())
                    .name(employeeCommand.getName() != null ? employeeCommand.getName() : existingEmployee.getName())
                    .surname(employeeCommand.getSurname() != null ? employeeCommand.getSurname() : existingEmployee.getSurname())
                    .pesel(employeeCommand.getPesel() != null ? employeeCommand.getPesel() : existingEmployee.getPesel())
                    .height(employeeCommand.getHeight() != 0.0 ? employeeCommand.getHeight() : existingEmployee.getHeight())
                    .weight(employeeCommand.getWeight() != 0.0 ? employeeCommand.getWeight() : existingEmployee.getWeight())
                    .emailAddress(employeeCommand.getEmailAddress() != null ? employeeCommand.getEmailAddress() : existingEmployee.getEmailAddress())
                    .version(employeeCommand.getVersion())
                    .build();

        } else {
            throw new IllegalArgumentException("Existing person is not an instance of Employee");
        }
    }
}