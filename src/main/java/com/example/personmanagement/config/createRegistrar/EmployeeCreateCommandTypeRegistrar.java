package com.example.personmanagement.config.createRegistrar;

import com.example.personmanagement.config.CommandTypeRegistrar;
import com.example.personmanagement.model.employee.CreateEmployeeCommand;
import com.example.personmanagement.model.person.CreatePersonCommand;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EmployeeCreateCommandTypeRegistrar implements CommandTypeRegistrar<CreatePersonCommand> {
    @Override
    public void registerCommandTypes(Map<String, Class<? extends CreatePersonCommand>> commandTypeMap) {
        commandTypeMap.put("EMPLOYEE", CreateEmployeeCommand.class);
    }
}