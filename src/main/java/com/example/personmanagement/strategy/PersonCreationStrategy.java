package com.example.personmanagement.strategy;

import com.example.personmanagement.model.person.CreatePersonCommand;
import com.example.personmanagement.model.person.Person;

public interface PersonCreationStrategy {

    Person create(CreatePersonCommand command);
}