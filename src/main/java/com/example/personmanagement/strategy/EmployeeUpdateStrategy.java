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

            if (employeeCommand.getName() != null) {
                existingEmployee.setName(employeeCommand.getName());
            }
            if (employeeCommand.getSurname() != null) {
                existingEmployee.setSurname(employeeCommand.getSurname());
            }
            if (employeeCommand.getPesel() != null) {
                existingEmployee.setPesel(employeeCommand.getPesel());
            }
            if (employeeCommand.getHeight() != 0.0) {
                existingEmployee.setHeight(employeeCommand.getHeight());
            }
            if (employeeCommand.getWeight() != 0.0) {
                existingEmployee.setWeight(employeeCommand.getWeight());
            }
            if (employeeCommand.getEmailAddress() != null) {
                existingEmployee.setEmailAddress(employeeCommand.getEmailAddress());
            }

            return existingEmployee;
        } else {
            throw new IllegalArgumentException("Existing person is not an instance of Employee");
        }
    }
}