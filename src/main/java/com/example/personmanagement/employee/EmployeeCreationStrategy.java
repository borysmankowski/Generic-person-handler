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
            if (employeeCommand.getEmploymentStartDate() != null) {
                existingEmployee.setEmploymentStartDate(employeeCommand.getEmploymentStartDate());
            }
            if (employeeCommand.getCurrentPosition() != null) {
                existingEmployee.setCurrentPosition(employeeCommand.getCurrentPosition());
            }
            if (employeeCommand.getCurrentSalary() != 0.0) {
                existingEmployee.setCurrentSalary(employeeCommand.getCurrentSalary());
            }

            return existingEmployee;
        } else {
            throw new IllegalArgumentException("Existing person is not an instance of Employee");
        }
    }

}

