package com.example.personmanagement.mapper.createRegistrar;

import com.example.personmanagement.employee.model.CreateEmployeeCommand;
import com.example.personmanagement.person.model.CreatePersonCommand;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EmployeeCreateCreateCommandTypeRegistrar implements CreateCommandTypeRegistrar {

    @Override
    public void registerCreateCommandTypes(Map<String, Class<? extends CreatePersonCommand>> commandTypeMap) {
        commandTypeMap.put("EMPLOYEE", CreateEmployeeCommand.class);
    }
}