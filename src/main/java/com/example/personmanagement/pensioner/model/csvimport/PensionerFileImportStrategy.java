package com.example.personmanagement.pensioner.model.csvimport;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.person.model.PersonFileImportStrategy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("pensionerFileImportStrategy")
public class PensionerFileImportStrategy implements PersonFileImportStrategy {

    @Override
    public void insert(String[] data, JdbcTemplate jdbcTemplate) {
        if (!"PENSIONER".equals(data[0])) {
            throw new InvalidStrategyTypeException("Invalid data type for PensionerFileImportStrategy");
        }

        String sql = "INSERT INTO person (type, name, surname, pesel, height, weight, email_address, pension_amount, worked_years, version) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                data[0],
                data[1],
                data[2],
                data[3],
                Double.parseDouble(data[4]),
                Double.parseDouble(data[5]),
                data[6],
                Double.parseDouble(data[7]),
                Integer.parseInt(data[8]),
                0
        );
    }

    @Override
    public void bulkInsert(List<String[]> dataList, JdbcTemplate jdbcTemplate) {
        String sql = "INSERT INTO person (type, name, surname, pesel, height, weight, email_address, pension_amount, worked_years, version) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        List<Object[]> batchArgs = dataList.stream()
                .map(data -> {
                    if (!"PENSIONER".equals(data[0])) {
                        throw new InvalidStrategyTypeException("Invalid data type for PensionerFileImportStrategy");
                    }
                    return new Object[]{
                            data[0],
                            data[1],
                            data[2],
                            data[3],
                            Double.parseDouble(data[4]),
                            Double.parseDouble(data[5]),
                            data[6],
                            Double.parseDouble(data[7]),
                            Integer.parseInt(data[8]),
                            0 // version
                    };
                })
                .toList();
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }
}