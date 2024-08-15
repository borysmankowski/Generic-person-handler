package com.example.personmanagement.strategy;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.model.person.Person;
import com.example.personmanagement.model.person.UpdatePersonCommand;
import com.example.personmanagement.model.student.Student;
import com.example.personmanagement.model.student.UpdateStudentCommand;
import org.springframework.stereotype.Component;

@Component("studentUpdateStrategy")
public class StudentUpdateStrategy implements PersonUpdateStrategy {
    @Override
    public Person update(Person existingPerson, UpdatePersonCommand command) {
        if (!(command instanceof UpdateStudentCommand studentCommand)) {
            throw new InvalidStrategyTypeException("Invalid command type for StudentUpdateStrategy");
        }
        if (existingPerson instanceof Student existingStudent) {

            return Student.builder()
                    .id(existingStudent.getId())
                    .type(existingStudent.getType())
                    .name(studentCommand.getName() != null ? studentCommand.getName() : existingStudent.getName())
                    .surname(studentCommand.getSurname() != null ? studentCommand.getSurname() : existingStudent.getSurname())
                    .pesel(studentCommand.getPesel() != null ? studentCommand.getPesel() : existingStudent.getPesel())
                    .height(studentCommand.getHeight() != 0.0 ? studentCommand.getHeight() : existingStudent.getHeight())
                    .weight(studentCommand.getWeight() != 0.0 ? studentCommand.getWeight() : existingStudent.getWeight())
                    .emailAddress(studentCommand.getEmailAddress() != null ? studentCommand.getEmailAddress() : existingStudent.getEmailAddress())
                    .nameOfUniversity(studentCommand.getNameOfUniversity() != null ? studentCommand.getNameOfUniversity() : existingStudent.getNameOfUniversity())
                    .yearOfStudies(studentCommand.getYearOfStudies() != 0 ? studentCommand.getYearOfStudies() : existingStudent.getYearOfStudies())
                    .courseName(studentCommand.getCourseName() != null ? studentCommand.getCourseName() : existingStudent.getCourseName())
                    .scholarship(studentCommand.getScholarship() != 0.0 ? studentCommand.getScholarship() : existingStudent.getScholarship())
                    .version(studentCommand.getVersion())
                    .build();
        } else {
            throw new IllegalArgumentException("Existing person is not an instance of Student");
        }
    }
}