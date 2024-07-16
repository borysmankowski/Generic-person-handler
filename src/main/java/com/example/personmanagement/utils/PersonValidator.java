package com.example.personmanagement.utils;

import com.example.personmanagement.exception.DuplicateResourceException;
import com.example.personmanagement.model.person.Person;
import com.example.personmanagement.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PersonValidator {
    private final PersonRepository personRepository;

    public void validate(Person person) {
        if (personRepository.existsByPesel(person.getPesel())) {
            throw new DuplicateResourceException("Person with this PESEL already exists!");
        }
    }
}