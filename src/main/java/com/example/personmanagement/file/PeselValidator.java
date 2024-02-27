package com.example.personmanagement.file;

import com.example.personmanagement.exception.DuplicateResourceException;
import com.example.personmanagement.person.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PeselValidator implements IValidator<String> {

    private final PersonRepository personRepository;

    @Override
    public void validate(String pesel) {
        if (personRepository.existsByPesel(pesel)) {
            throw new DuplicateResourceException("Pesel number already exists: " + pesel);
        }
    }
}

