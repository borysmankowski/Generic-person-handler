package com.example.personmanagement.mapper;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.person.model.CreatePersonCommand;
import com.example.personmanagement.person.model.Person;
import com.example.personmanagement.person.model.PersonDto;
import com.example.personmanagement.student.model.CreateStudentCommand;
import com.example.personmanagement.student.model.Student;
import com.example.personmanagement.student.model.StudentDto;
import org.springframework.stereotype.Component;

@Component
public class StudentMapper implements PersonTypeMapper {
    @Override
    public boolean supports(String entityType) {
        return "STUDENT".equals(entityType);
    }

    @Override
    public PersonDto toDto(Person person) {
        if (person instanceof Student student) {
            return StudentDto.builder()
                    .id(person.getId())
                    .name(person.getName())
                    .surname(person.getSurname())
                    .pesel(person.getPesel())
                    .height(person.getHeight())
                    .weight(person.getWeight())
                    .emailAddress(person.getEmailAddress())
                    .nameOfUniversity(student.getNameOfUniversity())
                    .yearOfStudies(student.getYearOfStudies())
                    .courseName(student.getCourseName())
                    .scholarship(student.getScholarship())
                    .build();
        }
        throw new InvalidStrategyTypeException("Unsupported type!");
    }

    @Override
    public Person fromDto(CreatePersonCommand command) {
        if ("STUDENT".equals(command.getType())) {
            CreateStudentCommand studentCommand = (CreateStudentCommand) command;
            return Student.builder()
                    .type(studentCommand.getType())
                    .name(studentCommand.getName())
                    .surname(studentCommand.getSurname())
                    .pesel(studentCommand.getPesel())
                    .weight(studentCommand.getWeight())
                    .height(studentCommand.getHeight())
                    .emailAddress(studentCommand.getEmailAddress())
                    .nameOfUniversity(studentCommand.getNameOfUniversity())
                    .yearOfStudies(studentCommand.getYearOfStudies())
                    .courseName(studentCommand.getCourseName())
                    .scholarship(studentCommand.getScholarship())
                    .build();
        }
        throw new InvalidStrategyTypeException("Unsupported type!");
    }
}
