package com.example.personmanagement.config.updateRegistrar;

import com.example.personmanagement.model.person.UpdatePersonCommand;

import java.util.Map;

public interface UpdateCommandTypeRegistrar {

    void registerUpdateCommandTypes(Map<String, Class<? extends UpdatePersonCommand>> commandTypeMap);
}