package com.example.personmanagement.strategy;

import com.example.personmanagement.model.person.Person;
import com.example.personmanagement.model.person.UpdatePersonCommand;

public interface PersonUpdateStrategy {
    Person update(long existingPerson, UpdatePersonCommand command);
}