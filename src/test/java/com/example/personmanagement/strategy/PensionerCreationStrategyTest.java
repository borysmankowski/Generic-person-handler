package com.example.personmanagement.strategy;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.model.pensioner.CreatePensionerCommand;
import com.example.personmanagement.model.pensioner.Pensioner;
import com.example.personmanagement.model.person.CreatePersonCommand;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PensionerCreationStrategyTest {
    private final PensionerCreationStrategy strategy = new PensionerCreationStrategy();

    @Test
    void create_withValidCreatePensionerCommand_shouldReturnPensioner() {

        CreatePensionerCommand command = new CreatePensionerCommand();
        command.setType("PENSIONER");
        command.setName("Alice");
        command.setSurname("Smith");
        command.setPesel("9876543210");
        command.setHeight(165);
        command.setWeight(60);
        command.setEmailAddress("alice.smith@example.com");
        command.setPensionAmount(1500.0);
        command.setWorkedYears(30);

        Pensioner result = (Pensioner) strategy.create(command);

        assertNotNull(result);
        assertEquals("PENSIONER", result.getType());
        assertEquals("Alice", result.getName());
        assertEquals("Smith", result.getSurname());
        assertEquals("9876543210", result.getPesel());
        assertEquals(165, result.getHeight());
        assertEquals(60, result.getWeight());
        assertEquals("alice.smith@example.com", result.getEmailAddress());
        assertEquals(1500.0, result.getPensionAmount());
        assertEquals(30, result.getWorkedYears());
    }

    @Test
    void create_withInvalidCommandType_shouldThrowInvalidStrategyTypeException() {

        CreatePersonCommand invalidCommand = new CreatePersonCommand() {
        };

        assertThrows(InvalidStrategyTypeException.class, () -> strategy.create(invalidCommand));
    }
}