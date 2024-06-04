package com.example.personmanagement.mapper;

import com.example.personmanagement.employee.model.CreateEmployeeCommand;
import com.example.personmanagement.employee.model.Employee;
import com.example.personmanagement.employee.model.EmployeeDto;
import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.person.model.CreatePersonCommand;
import com.example.personmanagement.person.model.Person;
import com.example.personmanagement.person.model.PersonDto;
import org.springframework.stereotype.Component;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.Set;

@Component
public class EmployeeMapper implements PersonTypeMapper {

    @Override
    public boolean supports(String entityType) {
        return "EMPLOYEE".equals(entityType);
    }

    @Override
    public PersonDto toDto(Person person) {
        if (person instanceof Employee employee) {

            return EmployeeDto.builder()
                    .id(person.getId())
                    .name(person.getName())
                    .surname(person.getSurname())
                    .pesel(person.getPesel())
                    .height(person.getHeight())
                    .weight(person.getWeight())
                    .emailAddress(person.getEmailAddress())
                    .employmentStartDate(employee.getEmploymentStartDate())
                    .currentPosition(employee.getCurrentPosition())
                    .currentSalary(employee.getCurrentSalary())
                    .numberOfJobPositions(Optional.ofNullable(employee.getJobPositions())
                            .map(Set::size)
                            .orElse(0))
                    .build();
        }
        throw new InvalidStrategyTypeException("Unsupported type!");
    }

    @Override
    public Person fromDto(CreatePersonCommand command) {
        if ("employee".equals(command.getType())) {
            CreateEmployeeCommand employeeCommand = (CreateEmployeeCommand) command;
            return Employee.builder()
                    .type(employeeCommand.getType())
                    .name(employeeCommand.getName())
                    .surname(employeeCommand.getSurname())
                    .pesel(employeeCommand.getPesel())
                    .weight(employeeCommand.getWeight())
                    .height(employeeCommand.getHeight())
                    .emailAddress(employeeCommand.getEmailAddress())
                    .employmentStartDate(employeeCommand.getEmploymentStartDate())
                    .currentPosition(employeeCommand.getCurrentPosition())
                    .currentSalary(employeeCommand.getCurrentSalary())
                    .numberOfJobPositions(0)
                    .build();
        }
        throw new InvalidStrategyTypeException("Unsupported type!");
    }

}