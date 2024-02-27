package com.example.personmanagement.student;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.person.PersonCreationStrategy;
import com.example.personmanagement.person.model.CreatePersonCommand;
import com.example.personmanagement.person.model.Person;
import com.example.personmanagement.student.model.CreateStudentCommand;
import com.example.personmanagement.student.model.Student;
import org.springframework.stereotype.Component;

@Component("STUDENT")
public class StudentCreationStrategy implements PersonCreationStrategy {

    @Override
    public Person create(CreatePersonCommand command) {
        if (!(command instanceof CreateStudentCommand studentCommand)) {
            throw new InvalidStrategyTypeException("Invalid command type for StudentCreationStrategy");
        }

        return Student.builder()
                .type(studentCommand.getType())
                .name(studentCommand.getName())
                .surname(studentCommand.getSurname())
                .pesel(studentCommand.getPesel())
                .height(studentCommand.getHeight())
                .weight(studentCommand.getWeight())
                .emailAddress(studentCommand.getEmailAddress())
                .nameOfUniversity(studentCommand.getNameOfUniversity())
                .yearOfStudies(studentCommand.getYearOfStudies())
                .courseName(studentCommand.getCourseName())
                .scholarship(studentCommand.getScholarship())
                .build();
    }
}
