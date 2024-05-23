package com.example.personmanagement.person;

import com.example.personmanagement.exception.DuplicateResourceException;
import com.example.personmanagement.person.model.Person;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class PersonValidator {
    private final PersonRepository personRepository;

    public void validate(Person person) {
        if (personRepository.existsByPesel(person.getPesel())) {
            throw new DuplicateResourceException("Person with this PESEL already exists!");
        }
    }

    public void validatePersonsForBatchSave(List<Person> persons) {
        persons.forEach(person -> {
            Stream<Person> personsWithSamePesel = persons.stream().filter(p -> person.getPesel().equals(p.getPesel()));
            if (personsWithSamePesel.count() > 1) {
                throw new DuplicateResourceException("Person with this PESEL already exists!");
            }
        });
    }
}