package com.example.personmanagement.person;

import com.example.personmanagement.person.model.Person;
import com.example.personmanagement.person.model.UpdatePersonCommand;

public interface PersonUpdateStrategy {
    Person update(Person existingPerson, UpdatePersonCommand command);

}