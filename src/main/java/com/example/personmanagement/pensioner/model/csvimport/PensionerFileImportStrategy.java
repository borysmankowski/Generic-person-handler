package com.example.personmanagement.pensioner.model.csvimport;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.pensioner.model.CreatePensionerCommand;
import com.example.personmanagement.person.model.CreatePersonCommand;
import com.example.personmanagement.person.model.PersonFileImportStrategy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component("pensionerFileImportStrategy")
public class PensionerFileImportStrategy implements PersonFileImportStrategy {

    @Override
    public void insert(CreatePersonCommand command, JdbcTemplate jdbcTemplate) {
        if (!(command instanceof CreatePensionerCommand pensionerCommand)) {
            throw new InvalidStrategyTypeException("Invalid command type for PensionerFileImportStrategy");
        }

        String sql = "INSERT INTO person (type, name, surname, pesel, height, weight, email_address, pension_amount, worked_years) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                pensionerCommand.getType(),
                pensionerCommand.getName(),
                pensionerCommand.getSurname(),
                pensionerCommand.getPesel(),
                pensionerCommand.getHeight(),
                pensionerCommand.getWeight(),
                pensionerCommand.getEmailAddress(),
                pensionerCommand.getPensionAmount(),
                pensionerCommand.getWorkedYears()
        );
    }
}