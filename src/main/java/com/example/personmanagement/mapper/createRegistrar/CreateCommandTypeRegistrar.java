package com.example.personmanagement.mapper.createRegistrar;

import com.example.personmanagement.person.model.CreatePersonCommand;

import java.util.Map;

public interface CreateCommandTypeRegistrar {
    void registerCreateCommandTypes(Map<String, Class<? extends CreatePersonCommand>> commandTypeMap);

}