package com.example.personmanagement.strategy;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.model.pensioner.Pensioner;
import com.example.personmanagement.model.pensioner.UpdatePensionerCommand;
import com.example.personmanagement.model.person.Person;
import com.example.personmanagement.model.person.UpdatePersonCommand;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PensionerUpdateStrategyTest {

    private final PensionerUpdateStrategy strategy = new PensionerUpdateStrategy();

    @Test
    void update_withValidUpdatePensionerCommand_shouldUpdatePensioner() {
        Pensioner existingPensioner = new Pensioner();
        existingPensioner.setName("Alice");
        existingPensioner.setSurname("Smith");
        existingPensioner.setPesel("9876543210");
        existingPensioner.setHeight(160);
        existingPensioner.setWeight(65);
        existingPensioner.setEmailAddress("alice.smith@example.com");
        existingPensioner.setPensionAmount(1500.0);
        existingPensioner.setWorkedYears(30);

        UpdatePensionerCommand command = new UpdatePensionerCommand();
        command.setName("Alice Updated");
        command.setPensionAmount(1600.0);

        Pensioner updatedPensioner = (Pensioner) strategy.update(existingPensioner, command);

        assertNotNull(updatedPensioner);
        assertEquals("Alice Updated", updatedPensioner.getName());
        assertEquals("Smith", updatedPensioner.getSurname());
        assertEquals("9876543210", updatedPensioner.getPesel());
        assertEquals(160, updatedPensioner.getHeight());
        assertEquals(65, updatedPensioner.getWeight());
        assertEquals("alice.smith@example.com", updatedPensioner.getEmailAddress());
        assertEquals(1600.0, updatedPensioner.getPensionAmount());
        assertEquals(30, updatedPensioner.getWorkedYears());
    }

    @Test
    void update_withInvalidCommandType_shouldThrowInvalidStrategyTypeException() {
        Person existingPerson = new Pensioner();

        UpdatePersonCommand invalidCommand = new UpdatePersonCommand() {
        };
        assertThrows(InvalidStrategyTypeException.class, () -> strategy.update(existingPerson, invalidCommand));
    }

    @Test
    void update_withInvalidExistingPersonType_shouldThrowIllegalArgumentException() {
        Person existingPerson = new Person() {
        };

        UpdatePensionerCommand validCommand = new UpdatePensionerCommand();
        assertThrows(IllegalArgumentException.class, () -> strategy.update(existingPerson, validCommand));
    }
}