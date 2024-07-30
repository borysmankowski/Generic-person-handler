package com.example.personmanagement.config.createRegistrar;

import com.example.personmanagement.model.person.CreatePersonCommand;

import java.util.Map;

public interface CreateCommandTypeRegistrar {
    void registerCreateCommandTypes(Map<String, Class<? extends CreatePersonCommand>> commandTypeMap);
}
