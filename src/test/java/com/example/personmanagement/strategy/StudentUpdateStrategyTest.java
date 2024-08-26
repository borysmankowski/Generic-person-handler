package com.example.personmanagement.strategy;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.model.person.Person;
import com.example.personmanagement.model.person.UpdatePersonCommand;
import com.example.personmanagement.model.student.Student;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StudentUpdateStrategyTest {
    private final StudentUpdateStrategy strategy = new StudentUpdateStrategy();

    @Test
    void update_withValidUpdateStudentCommand_shouldUpdateStudent() {

        Student existingStudent = new Student();
        existingStudent.setType("STUDENT");
        existingStudent.setName("Emily");
        existingStudent.setSurname("Davis");
        existingStudent.setPesel("5678901234");
        existingStudent.setHeight(170);
        existingStudent.setWeight(55);
        existingStudent.setEmailAddress("emily.davis@example.com");
        existingStudent.setCourseName("Computer Science");
        existingStudent.setNameOfUniversity("University of Example");
        existingStudent.setScholarship(1200);
        existingStudent.setYearOfStudies(3);

        HashMap<String, String> params1 = new HashMap<>();
        params1.put("nameOfUniversity", "UniName");
        params1.put("yearOfStudies", "2020");
        params1.put("courseName", "CourseName");
        params1.put("scholarship", "2000");

        UpdatePersonCommand command = new UpdatePersonCommand();
        command.setType("STUDENT");
        command.setName("Emily Updated");
        HashMap<String, String> params2 = new HashMap<>();
        params2.put("yearOfStudies", "2020");
        command.setPersonUniqueFields(params2);

        Student updatedStudent = (Student) strategy.update(existingStudent, command);

        assertNotNull(updatedStudent);
        assertEquals("Emily Updated", updatedStudent.getName());
        assertEquals("Davis", updatedStudent.getSurname());
        assertEquals("5678901234", updatedStudent.getPesel());
        assertEquals(170, updatedStudent.getHeight());
        assertEquals(55, updatedStudent.getWeight());
        assertEquals("emily.davis@example.com", updatedStudent.getEmailAddress());
        assertEquals("University of Example", updatedStudent.getNameOfUniversity());
        assertEquals(2020, updatedStudent.getYearOfStudies());
        assertEquals("Computer Science", updatedStudent.getCourseName());
        assertEquals(1200.0, updatedStudent.getScholarship());
    }

    @Test
    void update_withInvalidCommandType_shouldThrowInvalidStrategyTypeException() {

        String type = "INVALID";

        // Arrange
        Person existingPerson = new Student();

        UpdatePersonCommand invalidCommand = new UpdatePersonCommand() {
        };

        invalidCommand.setType(type);
        invalidCommand.setName("Emily");
        invalidCommand.setSurname("Davis");
        invalidCommand.setPesel("5678901234");
        invalidCommand.setHeight(170);
        invalidCommand.setWeight(55);
        invalidCommand.setEmailAddress("emily.davis@example.com");

        HashMap<String, String> params1 = new HashMap<>();
        params1.put("nameOfUniversity", "University of Example");
        params1.put("yearOfStudies", "2");
        params1.put("courseName", "Computer Science");
        params1.put("scholarship", "1200");
        invalidCommand.setPersonUniqueFields(params1);

        assertThrows(InvalidStrategyTypeException.class, () -> strategy.update(existingPerson, invalidCommand));
    }

    @Test
    void update_withInvalidExistingPersonType_shouldThrowIllegalArgumentException() {

        Person existingPerson = new Person() {
        };

        UpdatePersonCommand validCommand = new UpdatePersonCommand();
        assertThrows(IllegalArgumentException.class, () -> strategy.update(existingPerson, validCommand));
    }
}