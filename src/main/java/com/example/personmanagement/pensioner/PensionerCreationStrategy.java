package com.example.personmanagement.pensioner;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.pensioner.model.CreatePensionerCommand;
import com.example.personmanagement.pensioner.model.Pensioner;
import com.example.personmanagement.pensioner.model.UpdatePensionerCommand;
import com.example.personmanagement.person.PersonCreationStrategy;
import com.example.personmanagement.person.model.CreatePersonCommand;
import com.example.personmanagement.person.model.Person;
import com.example.personmanagement.person.model.UpdatePersonCommand;
import org.springframework.stereotype.Component;

@Component("PENSIONER")
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

    @Override
    public Person update(Person existingPerson, UpdatePersonCommand command) {
        if (!(command instanceof UpdatePensionerCommand pensionerCommand)) {
            throw new InvalidStrategyTypeException("Invalid command type for PensionerUpdateStrategy");
        }
        if (existingPerson instanceof Pensioner existingPensioner) {

            if (pensionerCommand.getName() != null) {
                existingPensioner.setName(pensionerCommand.getName());
            }
            if (pensionerCommand.getSurname() != null) {
                existingPensioner.setSurname(pensionerCommand.getSurname());
            }
            if (pensionerCommand.getPesel() != null) {
                existingPensioner.setPesel(pensionerCommand.getPesel());
            }
            if (pensionerCommand.getHeight() != 0.0) {
                existingPensioner.setHeight(pensionerCommand.getHeight());
            }
            if (pensionerCommand.getWeight() != 0.0) {
                existingPensioner.setWeight(pensionerCommand.getWeight());
            }
            if (pensionerCommand.getEmailAddress() != null) {
                existingPensioner.setEmailAddress(pensionerCommand.getEmailAddress());
            }
            if (pensionerCommand.getPensionAmount() != 0.0) {
                existingPensioner.setPensionAmount(pensionerCommand.getPensionAmount());
            }
            if (pensionerCommand.getWorkedYears() != 0) {
                existingPensioner.setWorkedYears(pensionerCommand.getWorkedYears());
            }

            return existingPensioner;
        } else {
            throw new IllegalArgumentException("Existing person is not an instance of Pensioner");
        }
    }
}

