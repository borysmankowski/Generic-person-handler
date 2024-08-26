package com.example.personmanagement.strategy;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.model.person.CreatePersonCommand;
import com.example.personmanagement.model.student.Student;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StudentCreationStrategyTest {
    private final StudentCreationStrategy strategy = new StudentCreationStrategy();

    @Test
    void create_withValidCreateStudentCommand_shouldReturnStudent() {

        CreatePersonCommand command = new CreatePersonCommand();
        command.setType("STUDENT");
        command.setName("Emily");
        command.setSurname("Davis");
        command.setPesel("5678901234");
        command.setHeight(170);
        command.setWeight(55);
        command.setEmailAddress("emily.davis@example.com");

        HashMap<String, String> params1 = new HashMap<>();
        params1.put("nameOfUniversity", "University of Example");
        params1.put("yearOfStudies", "2");
        params1.put("courseName", "Computer Science");
        params1.put("scholarship", "1200");

        command.setPersonUniqueFields(params1);

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

        String type = "INVALID";

        CreatePersonCommand invalidCommand = new CreatePersonCommand() {
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

        assertThrows(InvalidStrategyTypeException.class, () -> strategy.create(invalidCommand));
    }
}