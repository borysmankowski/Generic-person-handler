package com.example.personmanagement.config.updateRegistrar;

import com.example.personmanagement.config.CommandTypeRegistrar;
import com.example.personmanagement.model.pensioner.UpdatePensionerCommand;
import com.example.personmanagement.model.person.UpdatePersonCommand;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PensionerUpdateCommandTypeRegistrar implements CommandTypeRegistrar<UpdatePersonCommand> {

    @Override
    public void registerCommandTypes(Map<String, Class<? extends UpdatePersonCommand>> commandTypeMap) {
        commandTypeMap.put("PENSIONER", UpdatePensionerCommand.class);
    }
}