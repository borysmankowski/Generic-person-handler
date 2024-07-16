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