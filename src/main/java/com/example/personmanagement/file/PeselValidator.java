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
        if (!isValidPesel(pesel)) {
            throw new IllegalArgumentException("Invalid PESEL number: " + pesel);
        }
    }

    private boolean isValidPesel(String pesel) {
        if (pesel.length() != 11) {
            return false;
        }

        if (!pesel.matches("\\d+")) {
            return false;
        }

        int[] weights = {1, 3, 7, 9, 1, 3, 7, 9, 1, 3, 1};
        int sum = 0;
        for (int i = 0; i < 11; i++) {
            sum += Character.getNumericValue(pesel.charAt(i)) * weights[i];
        }
        return sum % 10 == 0;
    }
}

