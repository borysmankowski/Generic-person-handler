package com.example.personmanagement.person;

import com.example.personmanagement.person.model.CreatePersonCommand;
import com.example.personmanagement.person.model.Person;
import com.example.personmanagement.person.model.UpdatePersonCommand;

public interface PersonCreationStrategy {

    Person create(CreatePersonCommand command);

    Person update(Person existingPerson, UpdatePersonCommand command);


}
