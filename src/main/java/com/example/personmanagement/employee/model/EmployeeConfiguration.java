package com.example.personmanagement.employee.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class EmployeeConfiguration {

    private final ObjectMapper objectMapper;

    @PostConstruct
    public void objectMapper(){
        objectMapper.registerSubtypes(new NamedType(CreateEmployeeCommand.class,"EMPLOYEE"));
        objectMapper.registerSubtypes(new NamedType(UpdateEmployeeCommand.class,"EMPLOYEE"));
    }
}
