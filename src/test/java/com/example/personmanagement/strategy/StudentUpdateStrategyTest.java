package com.example.personmanagement.strategy;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.exception.ResourceNotFoundException;
import com.example.personmanagement.model.person.Person;
import com.example.personmanagement.model.person.UpdatePersonCommand;
import com.example.personmanagement.model.student.Student;
import com.example.personmanagement.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StudentUpdateStrategyTest {
    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private StudentUpdateStrategy strategy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void update_withValidUpdateStudentCommand_shouldUpdateStudent() {
        long existingPersonId = 1L;

        Student existingStudent = new Student();
        existingStudent.setId(existingPersonId);
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

        when(personRepository.findById(existingPersonId)).thenReturn(Optional.of(existingStudent));

        UpdatePersonCommand command = new UpdatePersonCommand();
        command.setType("STUDENT");
        command.setName("Emily Updated");

        HashMap<String, String> params = new HashMap<>();
        params.put("yearOfStudies", "2020");
        command.setPersonUniqueFields(params);

        Student updatedStudent = (Student) strategy.update(existingPersonId, command);

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

        verify(personRepository, times(1)).findById(existingPersonId);
    }

    @Test
    void update_withInvalidCommandType_shouldThrowInvalidStrategyTypeException() {
        long existingPersonId = 1L;

        Student existingStudent = new Student();
        existingStudent.setId(existingPersonId);
        existingStudent.setType("STUDENT");

        when(personRepository.findById(existingPersonId)).thenReturn(Optional.of(existingStudent));

        UpdatePersonCommand invalidCommand = new UpdatePersonCommand();
        invalidCommand.setType("INVALID");

        assertThrows(InvalidStrategyTypeException.class, () -> strategy.update(existingPersonId, invalidCommand));

        verify(personRepository, times(1)).findById(existingPersonId);
    }

    @Test
    void update_withNonExistentStudent_shouldThrowResourceNotFoundException() {
        long nonExistentPersonId = 99L;

        when(personRepository.findById(nonExistentPersonId)).thenReturn(Optional.empty());

        UpdatePersonCommand validCommand = new UpdatePersonCommand();
        validCommand.setType("STUDENT");

        assertThrows(ResourceNotFoundException.class, () -> strategy.update(nonExistentPersonId, validCommand));

        verify(personRepository, times(1)).findById(nonExistentPersonId);
    }
}