package com.example.personmanagement.mapper.updateRegistrar;

import com.example.personmanagement.person.model.UpdatePersonCommand;

import java.util.Map;

public interface UpdateCommandTypeRegistrar {

    void registerUpdateCommandTypes(Map<String, Class<? extends UpdatePersonCommand>> commandTypeMap);

}