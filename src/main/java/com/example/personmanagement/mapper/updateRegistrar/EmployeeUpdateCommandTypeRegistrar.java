package com.example.personmanagement.mapper.updateRegistrar;

import com.example.personmanagement.employee.model.UpdateEmployeeCommand;
import com.example.personmanagement.person.model.UpdatePersonCommand;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EmployeeUpdateCommandTypeRegistrar implements UpdateCommandTypeRegistrar {

    @Override
    public void registerUpdateCommandTypes(Map<String, Class<? extends UpdatePersonCommand>> commandTypeMap) {
        commandTypeMap.put("EMPLOYEE", UpdateEmployeeCommand.class);
    }
}