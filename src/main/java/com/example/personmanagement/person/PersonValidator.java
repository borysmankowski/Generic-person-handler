package com.example.personmanagement.person;

import com.example.personmanagement.exception.DuplicateResourceException;
import com.example.personmanagement.person.model.Person;
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