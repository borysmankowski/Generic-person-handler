package com.example.personmanagement.strategy;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.exception.ResourceNotFoundException;
import com.example.personmanagement.model.pensioner.Pensioner;
import com.example.personmanagement.model.person.Person;
import com.example.personmanagement.model.person.UpdatePersonCommand;
import com.example.personmanagement.model.student.Student;
import com.example.personmanagement.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("pensionerUpdateStrategy")
@RequiredArgsConstructor
public class PensionerUpdateStrategy implements PersonUpdateStrategy {

    private final PersonRepository personRepository;
    final String EXPECTED_TYPE = "PENSIONER";

    @Override
    public Person update(long existingPerson, UpdatePersonCommand command) {

        Pensioner existingPensioner = (Pensioner) personRepository.findById(existingPerson)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + existingPerson));

        if (command.getType() == null || !EXPECTED_TYPE.equals(command.getType())) {
            throw new InvalidStrategyTypeException("Invalid type for PensionerUpdateStrategy: expected " + EXPECTED_TYPE + " but got " + command.getType());
        }

        return Pensioner.builder()
                .id(existingPensioner.getId())
                .type(existingPensioner.getType())
                .name(command.getName() != null ? command.getName() : existingPensioner.getName())
                .surname(command.getSurname() != null ? command.getSurname() : existingPensioner.getSurname())
                .pesel(command.getPesel() != null ? command.getPesel() : existingPensioner.getPesel())
                .height(command.getHeight() != 0.0 ? command.getHeight() : existingPensioner.getHeight())
                .weight(command.getWeight() != 0.0 ? command.getWeight() : existingPensioner.getWeight())
                .emailAddress(command.getEmailAddress() != null ? command.getEmailAddress() : existingPensioner.getEmailAddress())
                .pensionAmount(Double.parseDouble(command.getPersonUniqueFields().getOrDefault("pensionAmount", String.valueOf(existingPensioner.getPensionAmount()))))
                .workedYears(Integer.parseInt(command.getPersonUniqueFields().getOrDefault("workedYears", String.valueOf(existingPensioner.getWorkedYears()))))
                .version(command.getVersion())
                .build();
    }
}