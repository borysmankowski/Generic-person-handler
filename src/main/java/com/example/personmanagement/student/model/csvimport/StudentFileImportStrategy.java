package com.example.personmanagement.student.model.csvimport;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.person.model.CreatePersonCommand;
import com.example.personmanagement.person.model.PersonFileImportStrategy;
import com.example.personmanagement.student.model.CreateStudentCommand;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component("studentFileImportStrategy")
public class StudentFileImportStrategy implements PersonFileImportStrategy {

    @Override
    public void insert(CreatePersonCommand command, JdbcTemplate jdbcTemplate) {
        if (!(command instanceof CreateStudentCommand studentCommand)) {
            throw new InvalidStrategyTypeException("Invalid command type for StudentFileImportStrategy");
        }

        String sql = "INSERT INTO person (type, name, surname, pesel, height, weight, email_address, name_of_university, year_of_studies, course_name, scholarship) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                studentCommand.getType(),
                studentCommand.getName(),
                studentCommand.getSurname(),
                studentCommand.getPesel(),
                studentCommand.getHeight(),
                studentCommand.getWeight(),
                studentCommand.getEmailAddress(),
                studentCommand.getNameOfUniversity(),
                studentCommand.getYearOfStudies(),
                studentCommand.getCourseName(),
                studentCommand.getScholarship()
        );
    }
}
