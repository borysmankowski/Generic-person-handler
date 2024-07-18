package com.example.personmanagement.strategy;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.model.person.CreatePersonCommand;
import com.example.personmanagement.model.student.CreateStudentCommand;
import com.example.personmanagement.model.student.Student;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StudentCreationStrategyTest {
    private final StudentCreationStrategy strategy = new StudentCreationStrategy();

    @Test
    void create_withValidCreateStudentCommand_shouldReturnStudent() {

        CreateStudentCommand command = new CreateStudentCommand();
        command.setType("STUDENT");
        command.setName("Emily");
        command.setSurname("Davis");
        command.setPesel("5678901234");
        command.setHeight(170);
        command.setWeight(55);
        command.setEmailAddress("emily.davis@example.com");
        command.setNameOfUniversity("University of Example");
        command.setYearOfStudies(2);
        command.setCourseName("Computer Science");
        command.setScholarship(1200.0);

        Student result = (Student) strategy.create(command);

        assertNotNull(result);
        assertEquals("STUDENT", result.getType());
        assertEquals("Emily", result.getName());
        assertEquals("Davis", result.getSurname());
        assertEquals("5678901234", result.getPesel());
        assertEquals(170, result.getHeight());
        assertEquals(55, result.getWeight());
        assertEquals("emily.davis@example.com", result.getEmailAddress());
        assertEquals("University of Example", result.getNameOfUniversity());
        assertEquals(2, result.getYearOfStudies());
        assertEquals("Computer Science", result.getCourseName());
        assertEquals(1200.0, result.getScholarship());
    }

    @Test
    void create_withInvalidCommandType_shouldThrowInvalidStrategyTypeException() {

        CreatePersonCommand invalidCommand = new CreatePersonCommand() {
        };

        assertThrows(InvalidStrategyTypeException.class, () -> strategy.create(invalidCommand));
    }
}