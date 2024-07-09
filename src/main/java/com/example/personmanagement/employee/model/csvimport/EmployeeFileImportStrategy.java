package com.example.personmanagement.employee.model.csvimport;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.person.model.PersonFileImportStrategy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("employeeFileImportStrategy")
public class EmployeeFileImportStrategy implements PersonFileImportStrategy {
    @Override
    public void bulkInsert(List<String[]> dataList, JdbcTemplate jdbcTemplate) {
        String sql = "INSERT INTO person (type, name, surname, pesel, height, weight, email_address,version) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        List<Object[]> batchArgs = dataList.stream()
                .map(data -> {
                    if (!"EMPLOYEE".equals(data[0])) {
                        throw new InvalidStrategyTypeException("Invalid data type for EmployeeFileImportStrategy");
                    }
                    return new Object[]{
                            data[0],
                            data[1],
                            data[2],
                            data[3],
                            Double.parseDouble(data[4]),
                            Double.parseDouble(data[5]),
                            data[6],
                            0
                    };
                })
                .toList();

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }
}