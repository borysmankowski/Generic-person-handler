package com.example.personmanagement.mapper;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.model.person.Person;
import com.example.personmanagement.model.student.Student;
import com.example.personmanagement.model.student.StudentDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StudentMapperTest {

    private final StudentMapper studentMapper = new StudentMapper();

    @Test
    void supports() {
        assertTrue(studentMapper.supports("STUDENT"));
        assertFalse(studentMapper.supports("PENSIONER"));
    }

    @Test
    void toDto() {
        Student student = Student.builder()
                .id(1L)
                .name("Charlie")
                .surname("Johnson")
                .pesel("1234567890")
                .height(170)
                .weight(60)
                .emailAddress("charlie.johnson@example.com")
                .nameOfUniversity("University of Example")
                .yearOfStudies(2)
                .courseName("Computer Science")
                .scholarship(500.0)
                .build();

        StudentDto studentDto = (StudentDto) studentMapper.toDto(student);

        assertEquals(1L, studentDto.getId());
        assertEquals("Charlie", studentDto.getName());
        assertEquals("Johnson", studentDto.getSurname());
        assertEquals("1234567890", studentDto.getPesel());
        assertEquals(170, studentDto.getHeight());
        assertEquals(60, studentDto.getWeight());
        assertEquals("charlie.johnson@example.com", studentDto.getEmailAddress());
        assertEquals("University of Example", studentDto.getNameOfUniversity());
        assertEquals(2, studentDto.getYearOfStudies());
        assertEquals("Computer Science", studentDto.getCourseName());
        assertEquals(500.0, studentDto.getScholarship());
    }

    @Test
    void toDto_withInvalidType() {
        Person person = new Person();
        person.setType("INVALID");

        assertThrows(InvalidStrategyTypeException.class, () -> studentMapper.toDto(person));
    }
}