package com.example.personmanagement.mapper;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.model.person.CreatePersonCommand;
import com.example.personmanagement.model.person.Person;
import com.example.personmanagement.model.person.PersonDto;
import com.example.personmanagement.model.student.CreateStudentCommand;
import com.example.personmanagement.model.student.Student;
import com.example.personmanagement.model.student.StudentDto;
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
}
