package com.example.personmanagement.employee.model.csvimport;

import com.example.personmanagement.employee.model.CreateEmployeeCommand;
import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.person.model.CreatePersonCommand;
import com.example.personmanagement.person.model.PersonFileImportStrategy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component("employeeFileImportStrategy")
public class EmployeeFileImportStrategy implements PersonFileImportStrategy {

    @Override
    public void insert(CreatePersonCommand command, JdbcTemplate jdbcTemplate) {
        if (!(command instanceof CreateEmployeeCommand employeeCommand)) {
            throw new InvalidStrategyTypeException("Invalid command type for EmployeeFileImportStrategy");
        }

        String sql = "INSERT INTO person (type, name, surname, pesel, height, weight, email_address, employment_start_date, current_position, current_salary) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                employeeCommand.getType(),
                employeeCommand.getName(),
                employeeCommand.getSurname(),
                employeeCommand.getPesel(),
                employeeCommand.getHeight(),
                employeeCommand.getWeight(),
                employeeCommand.getEmailAddress(),
                employeeCommand.getEmploymentStartDate(),
                employeeCommand.getCurrentPosition(),
                employeeCommand.getCurrentSalary()
        );
    }
}
