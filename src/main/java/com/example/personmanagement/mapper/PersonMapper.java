package com.example.personmanagement.mapper;

import com.example.personmanagement.model.person.Person;
import com.example.personmanagement.model.person.PersonDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PersonMapper {

    private final List<PersonTypeMapper> mappers;

    public PersonDto toDto(Person person) {
        for (PersonTypeMapper mapper : mappers) {
            if (mapper.supports(person.getType())) {
                return mapper.toDto(person);
            }
        }
        throw new IllegalArgumentException("Unsupported type!");
    }

}