package com.example.personmanagement.mapper;

import com.example.personmanagement.model.employee.CreateEmployeeCommand;
import com.example.personmanagement.model.employee.Employee;
import com.example.personmanagement.model.employee.EmployeeDto;
import com.example.personmanagement.model.position.JobPosition;
import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.model.person.CreatePersonCommand;
import com.example.personmanagement.model.person.Person;
import com.example.personmanagement.model.person.PersonDto;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EmployeeMapper implements PersonTypeMapper {

    @Override
    public boolean supports(String entityType) {
        return "EMPLOYEE".equals(entityType);
    }

    @Override
    public PersonDto toDto(Person person) {
        if (person instanceof Employee employee) {
            Optional<String> currentJobPosition = Optional.ofNullable(employee.getJobPositions())
                    .flatMap(jobPositions -> jobPositions.stream()
                            .filter(jobPosition -> jobPosition.getEndDate() == null)
                            .map(JobPosition::getPositionName).findFirst());

            return EmployeeDto.builder()
                    .id(person.getId())
                    .name(person.getName())
                    .surname(person.getSurname())
                    .pesel(person.getPesel())
                    .height(person.getHeight())
                    .weight(person.getWeight())
                    .emailAddress(person.getEmailAddress())
                    .currentJobPosition(currentJobPosition.orElse(null))
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
                    .build();
        }
        throw new InvalidStrategyTypeException("Unsupported type!");
    }

}