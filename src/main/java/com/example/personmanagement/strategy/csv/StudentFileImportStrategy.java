package com.example.personmanagement.strategy.csv;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.strategy.PersonFileImportStrategy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("studentFileImportStrategy")
public class StudentFileImportStrategy implements PersonFileImportStrategy {
    @Override
    public void bulkInsert(List<String[]> dataList, JdbcTemplate jdbcTemplate) {
        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO person (type, name, surname, pesel, height, weight, email_address, name_of_university, year_of_studies, course_name, scholarship, version) VALUES ");

        for (int i = 0; i < dataList.size(); i++) {
            String[] data = dataList.get(i);
            if (!"STUDENT".equals(data[0])) {
                throw new InvalidStrategyTypeException("Invalid data type for StudentFileImportStrategy");
            }

            sqlBuilder.append(String.format("('%s', '%s', '%s', '%s', %s, %s, '%s', '%s', %d, '%s', %s, %d)",
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
                    0
            ));

            if (i < dataList.size() - 1) {
                sqlBuilder.append(", ");
            }
        }
        jdbcTemplate.update(sqlBuilder.toString());
    }
}