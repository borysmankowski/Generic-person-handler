package com.example.personmanagement.strategy;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.model.pensioner.Pensioner;
import com.example.personmanagement.model.person.CreatePersonCommand;
import com.example.personmanagement.model.person.Person;
import org.springframework.stereotype.Component;

@Component("pensionerCreationStrategy")
public class PensionerCreationStrategy implements PersonCreationStrategy {

    private static final String EXPECTED_TYPE = "PENSIONER";

    @Override
    public Person create(CreatePersonCommand command) {
        if (!EXPECTED_TYPE.equals(command.getType())) {
            throw new InvalidStrategyTypeException("Invalid type for PensionerCreationStrategy: expected " + EXPECTED_TYPE + " but got " + command.getType());
        }

        return Pensioner.builder()
                .type(command.getType())
                .name(command.getName())
                .surname(command.getSurname())
                .pesel(command.getPesel())
                .height(command.getHeight())
                .weight(command.getWeight())
                .emailAddress(command.getEmailAddress())
                .pensionAmount(Double.parseDouble(command.getPersonUniqueFields().get("pensionAmount")))
                .workedYears(Integer.parseInt(command.getPersonUniqueFields().get("workedYears")))
                .build();
    }
}