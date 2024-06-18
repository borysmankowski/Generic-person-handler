package com.example.personmanagement.student.model.csvimport;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.person.model.PersonFileImportStrategy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component("studentFileImportStrategy")
public class StudentFileImportStrategy implements PersonFileImportStrategy {

    @Override
    public void insert(String[] data, JdbcTemplate jdbcTemplate) {
        if (!"STUDENT".equals(data[0])) {
            throw new InvalidStrategyTypeException("Invalid data type for StudentFileImportStrategy");
        }

        String sql = "INSERT INTO person (type, name, surname, pesel, height, weight, email_address, name_of_university, year_of_studies, course_name, scholarship, version) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                data[0],
                data[1],
                data[2],
                data[3],
                Double.parseDouble(data[4]),
                Double.parseDouble(data[5]),
                data[6],
                data[7],
                Integer.parseInt(data[8]),
                data[9],
                Double.parseDouble(data[10]),
                0 // version
        );
    }
}
