package com.example.personmanagement.mapper;

import com.example.personmanagement.person.model.CreatePersonCommand;
import com.example.personmanagement.person.model.Person;
import com.example.personmanagement.person.model.PersonDto;

public interface PersonTypeMapper {
    boolean supports(String entityType);

    PersonDto toDto(Person person);

    Person fromDto(CreatePersonCommand command);
}
