package com.example.personmanagement.strategy;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.model.person.Person;
import com.example.personmanagement.model.person.UpdatePersonCommand;
import com.example.personmanagement.model.student.Student;
import org.springframework.stereotype.Component;

@Component("studentUpdateStrategy")
public class StudentUpdateStrategy implements PersonUpdateStrategy {

    final String EXPECTED_TYPE = "STUDENT";

    @Override
    public Person update(Person existingPerson, UpdatePersonCommand command) {
        if (!(existingPerson instanceof Student existingStudent)) {
            throw new IllegalArgumentException("Existing person is not an instance of Student");
        }

        if (command.getType() == null || !EXPECTED_TYPE.equals(command.getType())) {
            throw new InvalidStrategyTypeException("Invalid type for StudentUpdateStrategy: expected " + EXPECTED_TYPE + " but got " + command.getType());
        }

        return Student.builder()
                .id(existingStudent.getId())
                .type(existingStudent.getType())
                .name(command.getName() != null ? command.getName() : existingStudent.getName())
                .surname(command.getSurname() != null ? command.getSurname() : existingStudent.getSurname())
                .pesel(command.getPesel() != null ? command.getPesel() : existingStudent.getPesel())
                .height(command.getHeight() != 0.0 ? command.getHeight() : existingStudent.getHeight())
                .weight(command.getWeight() != 0.0 ? command.getWeight() : existingStudent.getWeight())
                .emailAddress(command.getEmailAddress() != null ? command.getEmailAddress() : existingStudent.getEmailAddress())
                .nameOfUniversity(command.getPersonUniqueFields().getOrDefault("nameOfUniversity", existingStudent.getNameOfUniversity()))
                .yearOfStudies(Integer.parseInt(command.getPersonUniqueFields().getOrDefault("yearOfStudies", String.valueOf(existingStudent.getYearOfStudies()))))
                .courseName(command.getPersonUniqueFields().getOrDefault("courseName", existingStudent.getCourseName()))
                .scholarship(Double.parseDouble(command.getPersonUniqueFields().getOrDefault("scholarship", String.valueOf(existingStudent.getScholarship()))))
                .version(command.getVersion())
                .build();
    }
}