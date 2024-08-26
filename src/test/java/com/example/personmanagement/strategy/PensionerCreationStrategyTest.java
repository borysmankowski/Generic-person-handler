package com.example.personmanagement.strategy;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.model.pensioner.Pensioner;
import com.example.personmanagement.model.person.CreatePersonCommand;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PensionerCreationStrategyTest {
    private final PensionerCreationStrategy strategy = new PensionerCreationStrategy();

    @Test
    void create_withValidCreatePensionerCommand_shouldReturnPensioner() {

        CreatePersonCommand command = new CreatePersonCommand();
        command.setType("PENSIONER");
        command.setName("Alice");
        command.setSurname("Smith");
        command.setPesel("9876543210");
        command.setHeight(165);
        command.setWeight(60);
        command.setEmailAddress("alice.smith@example.com");

        HashMap<String, String> params1 = new HashMap<>();
        params1.put("pensionAmount", "1500");
        params1.put("workedYears", "30");
        command.setPersonUniqueFields(params1);

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
        params1.put("pensionAmount", "1500");
        params1.put("workedYears", "30");
        invalidCommand.setPersonUniqueFields(params1);

        invalidCommand.setPersonUniqueFields(params1);
        assertThrows(InvalidStrategyTypeException.class, () -> strategy.create(invalidCommand));
    }
}