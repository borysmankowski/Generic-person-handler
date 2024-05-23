package com.example.personmanagement.mapper.updateRegistrar;

import com.example.personmanagement.pensioner.model.UpdatePensionerCommand;
import com.example.personmanagement.person.model.UpdatePersonCommand;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component

public class PensionerUpdateCommandTypeRegistrar implements UpdateCommandTypeRegistrar {

    @Override
    public void registerUpdateCommandTypes(Map<String, Class<? extends UpdatePersonCommand>> commandTypeMap) {
        commandTypeMap.put("PENSIONER", UpdatePensionerCommand.class);
    }
}