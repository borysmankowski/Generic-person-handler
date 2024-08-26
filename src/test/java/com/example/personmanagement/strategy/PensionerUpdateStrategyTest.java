package com.example.personmanagement.strategy;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.model.pensioner.Pensioner;
import com.example.personmanagement.model.person.Person;
import com.example.personmanagement.model.person.UpdatePersonCommand;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

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

        UpdatePersonCommand command = new UpdatePersonCommand();
        command.setType("PENSIONER");
        command.setName("Alice Updated");
        HashMap<String, String> params1 = new HashMap<>();
        params1.put("pensionAmount", "1600.0");

        command.setPersonUniqueFields(params1);

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

        invalidCommand.setType("INVALID");
        invalidCommand.setName("Alice");
        invalidCommand.setSurname("Smith");
        invalidCommand.setPesel("9876543210");
        invalidCommand.setHeight(160);
        invalidCommand.setWeight(65);
        invalidCommand.setEmailAddress("alice.smith@example.com");

        HashMap<String, String> params1 = new HashMap<>();
        params1.put("pensionAmount", "1500");
        params1.put("workedYears", "30");

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