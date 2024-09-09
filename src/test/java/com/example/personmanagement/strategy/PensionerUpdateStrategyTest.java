package com.example.personmanagement.strategy;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.exception.ResourceNotFoundException;
import com.example.personmanagement.model.pensioner.Pensioner;
import com.example.personmanagement.model.person.Person;
import com.example.personmanagement.model.person.UpdatePersonCommand;
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

class PensionerUpdateStrategyTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private PensionerUpdateStrategy strategy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void update_withValidUpdatePensionerCommand_shouldUpdatePensioner() {
        long existingPersonId = 1L;

        Pensioner existingPensioner = new Pensioner();
        existingPensioner.setId(existingPersonId);
        existingPensioner.setName("Alice");
        existingPensioner.setSurname("Smith");
        existingPensioner.setPesel("9876543210");
        existingPensioner.setHeight(160);
        existingPensioner.setWeight(65);
        existingPensioner.setEmailAddress("alice.smith@example.com");
        existingPensioner.setPensionAmount(1500.0);
        existingPensioner.setWorkedYears(30);

        when(personRepository.findById(existingPersonId)).thenReturn(Optional.of(existingPensioner));

        UpdatePersonCommand command = new UpdatePersonCommand();
        command.setType("PENSIONER");
        command.setName("Alice Updated");

        HashMap<String, String> params1 = new HashMap<>();
        params1.put("pensionAmount", "1600.0");
        command.setPersonUniqueFields(params1);

        Pensioner updatedPensioner = (Pensioner) strategy.update(existingPersonId, command);

        assertNotNull(updatedPensioner);
        assertEquals("Alice Updated", updatedPensioner.getName());
        assertEquals("Smith", updatedPensioner.getSurname());
        assertEquals("9876543210", updatedPensioner.getPesel());
        assertEquals(160, updatedPensioner.getHeight());
        assertEquals(65, updatedPensioner.getWeight());
        assertEquals("alice.smith@example.com", updatedPensioner.getEmailAddress());
        assertEquals(1600.0, updatedPensioner.getPensionAmount());
        assertEquals(30, updatedPensioner.getWorkedYears());

        verify(personRepository, times(1)).findById(existingPersonId);
    }

    @Test
    void update_withInvalidCommandType_shouldThrowInvalidStrategyTypeException() {
        long existingPersonId = 1L;

        Pensioner existingPensioner = new Pensioner();
        existingPensioner.setId(existingPersonId);

        when(personRepository.findById(existingPersonId)).thenReturn(Optional.of(existingPensioner));

        UpdatePersonCommand invalidCommand = new UpdatePersonCommand();
        invalidCommand.setType("INVALID");  // Invalid type for Pensioner strategy

        assertThrows(InvalidStrategyTypeException.class, () -> strategy.update(existingPersonId, invalidCommand));

        verify(personRepository, times(1)).findById(existingPersonId);
    }

    @Test
    void update_withNonExistentPensioner_shouldThrowResourceNotFoundException() {
        long nonExistentPersonId = 99L;

        when(personRepository.findById(nonExistentPersonId)).thenReturn(Optional.empty());

        UpdatePersonCommand validCommand = new UpdatePersonCommand();
        validCommand.setType("PENSIONER");

        assertThrows(ResourceNotFoundException.class, () -> strategy.update(nonExistentPersonId, validCommand));

        verify(personRepository, times(1)).findById(nonExistentPersonId);
    }
}