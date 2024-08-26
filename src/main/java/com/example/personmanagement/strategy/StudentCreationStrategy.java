package com.example.personmanagement.strategy;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.model.person.CreatePersonCommand;
import com.example.personmanagement.model.person.Person;
import com.example.personmanagement.model.student.Student;
import org.springframework.stereotype.Component;

@Component("studentCreationStrategy")
public class StudentCreationStrategy implements PersonCreationStrategy {

    private static final String EXPECTED_TYPE = "STUDENT";

    @Override
    public Person create(CreatePersonCommand command) {
        if (!EXPECTED_TYPE.equals(command.getType())) {
            throw new InvalidStrategyTypeException("Invalid type for StudentCreationStrategy: expected " + EXPECTED_TYPE + " but got " + command.getType());
        }

        return Student.builder()
                .type(command.getType())
                .type(command.getType())
                .name(command.getName())
                .surname(command.getSurname())
                .pesel(command.getPesel())
                .height(command.getHeight())
                .weight(command.getWeight())
                .emailAddress(command.getEmailAddress())
                .nameOfUniversity(command.getPersonUniqueFields().get("nameOfUniversity"))
                .yearOfStudies(Integer.parseInt(command.getPersonUniqueFields().get("yearOfStudies")))
                .courseName(command.getPersonUniqueFields().get("courseName"))
                .scholarship(Double.parseDouble(command.getPersonUniqueFields().get("scholarship")))
                .build();
    }
}