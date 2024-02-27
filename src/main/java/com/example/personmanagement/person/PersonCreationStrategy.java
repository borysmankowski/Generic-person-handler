package com.example.personmanagement.person;

import com.example.personmanagement.person.model.CreatePersonCommand;
import com.example.personmanagement.person.model.Person;

public interface PersonCreationStrategy {

    Person create(CreatePersonCommand command);


}
