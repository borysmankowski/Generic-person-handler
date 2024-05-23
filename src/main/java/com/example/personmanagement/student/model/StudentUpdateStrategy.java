package com.example.personmanagement.student;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.person.PersonUpdateStrategy;
import com.example.personmanagement.person.model.Person;
import com.example.personmanagement.person.model.UpdatePersonCommand;
import com.example.personmanagement.student.model.Student;
import com.example.personmanagement.student.model.UpdateStudentCommand;
import org.springframework.stereotype.Component;

@Component("studentUpdateStrategy")
public class StudentUpdateStrategy implements PersonUpdateStrategy {

    @Override
    public Person update(Person existingPerson, UpdatePersonCommand command) {
        if (!(command instanceof UpdateStudentCommand studentCommand)) {
            throw new InvalidStrategyTypeException("Invalid command type for StudentUpdateStrategy");
        }
        if (existingPerson instanceof Student existingStudent) {

            if (studentCommand.getName() != null) {
                existingStudent.setName(studentCommand.getName());
            }
            if (studentCommand.getSurname() != null) {
                existingStudent.setSurname(studentCommand.getSurname());
            }
            if (studentCommand.getPesel() != null) {
                existingStudent.setPesel(studentCommand.getPesel());
            }
            if (studentCommand.getHeight() != 0.0) {
                existingStudent.setHeight(studentCommand.getHeight());
            }
            if (studentCommand.getWeight() != 0.0) {
                existingStudent.setWeight(studentCommand.getWeight());
            }
            if (studentCommand.getEmailAddress() != null) {
                existingStudent.setEmailAddress(studentCommand.getEmailAddress());
            }
            if (studentCommand.getNameOfUniversity() != null) {
                existingStudent.setNameOfUniversity(studentCommand.getNameOfUniversity());
            }
            if (studentCommand.getYearOfStudies() != 0) {
                existingStudent.setYearOfStudies(studentCommand.getYearOfStudies());
            }
            if (studentCommand.getCourseName() != null) {
                existingStudent.setCourseName(studentCommand.getCourseName());
            }
            if (studentCommand.getScholarship() != 0.0) {
                existingStudent.setScholarship(studentCommand.getScholarship());
            }

            return existingStudent;
        } else {
            throw new IllegalArgumentException("Existing person is not an instance of Student");
        }
    }
}