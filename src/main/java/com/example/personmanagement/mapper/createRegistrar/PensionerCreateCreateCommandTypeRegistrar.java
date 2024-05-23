package com.example.personmanagement.mapper.createRegistrar;

import com.example.personmanagement.pensioner.model.CreatePensionerCommand;
import com.example.personmanagement.person.model.CreatePersonCommand;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PensionerCreateCreateCommandTypeRegistrar implements CreateCommandTypeRegistrar {

    @Override
    public void registerCreateCommandTypes(Map<String, Class<? extends CreatePersonCommand>> commandTypeMap) {
        commandTypeMap.put("PENSIONER", CreatePensionerCommand.class);
    }
}