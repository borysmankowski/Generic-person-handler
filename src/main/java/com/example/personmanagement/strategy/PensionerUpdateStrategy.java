package com.example.personmanagement.strategy;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.model.pensioner.Pensioner;
import com.example.personmanagement.model.pensioner.UpdatePensionerCommand;
import com.example.personmanagement.model.person.Person;
import com.example.personmanagement.model.person.UpdatePersonCommand;
import org.springframework.stereotype.Component;

@Component("pensionerUpdateStrategy")
public class PensionerUpdateStrategy implements PersonUpdateStrategy {
    @Override
    public Person update(Person existingPerson, UpdatePersonCommand command) {
        if (!(command instanceof UpdatePensionerCommand pensionerCommand)) {
            throw new InvalidStrategyTypeException("Invalid command type for PensionerUpdateStrategy");
        }
        if (existingPerson instanceof Pensioner existingPensioner) {

            return Pensioner.builder()
                    .id(existingPensioner.getId())
                    .type(existingPensioner.getType())
                    .name(pensionerCommand.getName() != null ? pensionerCommand.getName() : existingPensioner.getName())
                    .surname(pensionerCommand.getSurname() != null ? pensionerCommand.getSurname() : existingPensioner.getSurname())
                    .pesel(pensionerCommand.getPesel() != null ? pensionerCommand.getPesel() : existingPensioner.getPesel())
                    .height(pensionerCommand.getHeight() != 0.0 ? pensionerCommand.getHeight() : existingPensioner.getHeight())
                    .weight(pensionerCommand.getWeight() != 0.0 ? pensionerCommand.getWeight() : existingPensioner.getWeight())
                    .emailAddress(pensionerCommand.getEmailAddress() != null ? pensionerCommand.getEmailAddress() : existingPensioner.getEmailAddress())
                    .pensionAmount(pensionerCommand.getPensionAmount() != 0.0 ? pensionerCommand.getPensionAmount() : existingPensioner.getPensionAmount())
                    .workedYears(pensionerCommand.getWorkedYears() != 0 ? pensionerCommand.getWorkedYears() : existingPensioner.getWorkedYears())
                    .version(pensionerCommand.getVersion())
                    .build();
        } else {
            throw new IllegalArgumentException("Existing person is not an instance of Pensioner");
        }
    }
}