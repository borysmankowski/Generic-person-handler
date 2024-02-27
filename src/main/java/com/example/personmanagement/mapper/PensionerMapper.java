package com.example.personmanagement.mapper;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.pensioner.model.CreatePensionerCommand;
import com.example.personmanagement.pensioner.model.Pensioner;
import com.example.personmanagement.pensioner.model.PensionerDto;
import com.example.personmanagement.person.model.CreatePersonCommand;
import com.example.personmanagement.person.model.Person;
import com.example.personmanagement.person.model.PersonDto;
import org.springframework.stereotype.Component;

@Component
public class PensionerMapper implements PersonTypeMapper {


    @Override
    public boolean supports(String entityType) {
        return "PENSIONER".equals(entityType);
    }

    @Override
    public PersonDto toDto(Person person) {
        if (person instanceof Pensioner pensioner) {
            return PensionerDto.builder()
                    .id(person.getId())
                    .name(person.getName())
                    .surname(person.getSurname())
                    .pesel(person.getPesel())
                    .height(person.getHeight())
                    .weight(person.getWeight())
                    .emailAddress(person.getEmailAddress())
                    .pensionAmount(pensioner.getPensionAmount())
                    .workedYears(pensioner.getWorkedYears())
                    .build();
        }
        throw new InvalidStrategyTypeException("Unsupported type!");
    }

    @Override
    public Person fromDto(CreatePersonCommand command) {
        if ("PENSIONER".equals(command.getType())) {
            CreatePensionerCommand pensionerCommand = (CreatePensionerCommand) command;
            return Pensioner.builder()
                    .type(pensionerCommand.getType())
                    .name(pensionerCommand.getName())
                    .surname(pensionerCommand.getSurname())
                    .pesel(pensionerCommand.getPesel())
                    .weight(pensionerCommand.getWeight())
                    .height(pensionerCommand.getHeight())
                    .emailAddress(pensionerCommand.getEmailAddress())
                    .pensionAmount(pensionerCommand.getPensionAmount())
                    .workedYears(pensionerCommand.getWorkedYears())
                    .build();
        }
        throw new InvalidStrategyTypeException("Unsupported type!");
    }

}
