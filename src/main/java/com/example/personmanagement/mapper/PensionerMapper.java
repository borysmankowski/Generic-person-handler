package com.example.personmanagement.mapper;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.model.pensioner.Pensioner;
import com.example.personmanagement.model.pensioner.PensionerDto;
import com.example.personmanagement.model.person.Person;
import com.example.personmanagement.model.person.PersonDto;
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
}
