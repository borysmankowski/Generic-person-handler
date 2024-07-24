package com.example.personmanagement.config.createRegistrar;

import com.example.personmanagement.model.pensioner.CreatePensionerCommand;
import com.example.personmanagement.model.person.CreatePersonCommand;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PensionerCreateCreateCommandTypeRegistrar implements CreateCommandTypeRegistrar {

    @Override
    public void registerCreateCommandTypes(Map<String, Class<? extends CreatePersonCommand>> commandTypeMap) {
        commandTypeMap.put("PENSIONER", CreatePensionerCommand.class);
    }
}
