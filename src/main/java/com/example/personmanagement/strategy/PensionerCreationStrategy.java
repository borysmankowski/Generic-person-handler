package com.example.personmanagement.strategy;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.model.pensioner.CreatePensionerCommand;
import com.example.personmanagement.model.pensioner.Pensioner;
import com.example.personmanagement.model.person.CreatePersonCommand;
import com.example.personmanagement.model.person.Person;
import org.springframework.stereotype.Component;

@Component("pensionerCreationStrategy")
public class PensionerCreationStrategy implements PersonCreationStrategy {

    @Override
    public Person create(CreatePersonCommand command) {
        if (!(command instanceof CreatePensionerCommand pensionerCommand)) {
            throw new InvalidStrategyTypeException("Invalid command type for PensionerCreationStrategy");
        }

        return Pensioner.builder()
                .type(pensionerCommand.getType())
                .name(pensionerCommand.getName())
                .surname(pensionerCommand.getSurname())
                .pesel(pensionerCommand.getPesel())
                .height(pensionerCommand.getHeight())
                .weight(pensionerCommand.getWeight())
                .emailAddress(pensionerCommand.getEmailAddress())
                .pensionAmount(pensionerCommand.getPensionAmount())
                .workedYears(pensionerCommand.getWorkedYears())
                .build();
    }


}