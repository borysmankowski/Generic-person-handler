package com.example.personmanagement.config.updateRegistrar;

import com.example.personmanagement.model.pensioner.UpdatePensionerCommand;
import com.example.personmanagement.model.person.UpdatePersonCommand;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component

public class PensionerUpdateCommandTypeRegistrar implements UpdateCommandTypeRegistrar {

    @Override
    public void registerUpdateCommandTypes(Map<String, Class<? extends UpdatePersonCommand>> commandTypeMap) {
        commandTypeMap.put("PENSIONER", UpdatePensionerCommand.class);
    }
}