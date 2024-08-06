package com.example.personmanagement.mapper;

import com.example.personmanagement.model.person.Person;
import com.example.personmanagement.model.person.PersonDto;

public interface PersonTypeMapper {
    boolean supports(String entityType);

    PersonDto toDto(Person person);
}
