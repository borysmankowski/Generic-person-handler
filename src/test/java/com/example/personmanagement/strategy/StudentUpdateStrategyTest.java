package com.example.personmanagement.strategy;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.model.person.Person;
import com.example.personmanagement.model.person.UpdatePersonCommand;
import com.example.personmanagement.model.student.Student;
import com.example.personmanagement.model.student.UpdateStudentCommand;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StudentUpdateStrategyTest {
    private final StudentUpdateStrategy strategy = new StudentUpdateStrategy();

    @Test
    void update_withValidUpdateStudentCommand_shouldUpdateStudent() {

        Student existingStudent = new Student();
        existingStudent.setName("Emily");
        existingStudent.setSurname("Davis");
        existingStudent.setPesel("5678901234");
        existingStudent.setHeight(170);
        existingStudent.setWeight(55);
        existingStudent.setEmailAddress("emily.davis@example.com");
        existingStudent.setNameOfUniversity("University of Example");
        existingStudent.setYearOfStudies(2);
        existingStudent.setCourseName("Computer Science");
        existingStudent.setScholarship(1200.0);

        UpdateStudentCommand command = new UpdateStudentCommand();
        command.setName("Emily Updated");
        command.setYearOfStudies(3);

        Student updatedStudent = (Student) strategy.update(existingStudent, command);

        assertNotNull(updatedStudent);
        assertEquals("Emily Updated", updatedStudent.getName());
        assertEquals("Davis", updatedStudent.getSurname());
        assertEquals("5678901234", updatedStudent.getPesel());
        assertEquals(170, updatedStudent.getHeight());
        assertEquals(55, updatedStudent.getWeight());
        assertEquals("emily.davis@example.com", updatedStudent.getEmailAddress());
        assertEquals("University of Example", updatedStudent.getNameOfUniversity());
        assertEquals(3, updatedStudent.getYearOfStudies());
        assertEquals("Computer Science", updatedStudent.getCourseName());
        assertEquals(1200.0, updatedStudent.getScholarship());
    }

    @Test
    void update_withInvalidCommandType_shouldThrowInvalidStrategyTypeException() {
        // Arrange
        Person existingPerson = new Student();

        UpdatePersonCommand invalidCommand = new UpdatePersonCommand() {
        };
        assertThrows(InvalidStrategyTypeException.class, () -> strategy.update(existingPerson, invalidCommand));
    }

    @Test
    void update_withInvalidExistingPersonType_shouldThrowIllegalArgumentException() {

        Person existingPerson = new Person() {
        };

        UpdateStudentCommand validCommand = new UpdateStudentCommand();
        assertThrows(IllegalArgumentException.class, () -> strategy.update(existingPerson, validCommand));
    }
}