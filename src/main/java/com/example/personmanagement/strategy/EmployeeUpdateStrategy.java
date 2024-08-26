package com.example.personmanagement.strategy;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.model.employee.Employee;
import com.example.personmanagement.model.person.Person;
import com.example.personmanagement.model.person.UpdatePersonCommand;
import org.springframework.stereotype.Component;

@Component("employeeUpdateStrategy")
public class EmployeeUpdateStrategy implements PersonUpdateStrategy {

    final String EXPECTED_TYPE = "EMPLOYEE";

    @Override
    public Person update(Person existingPerson, UpdatePersonCommand command) {
        if (!(existingPerson instanceof Employee existingEmployee)) {
            throw new InvalidStrategyTypeException("Existing person is not an instance of Employee");
        }

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
                .version(command.getVersion())
                .build();
    }
}