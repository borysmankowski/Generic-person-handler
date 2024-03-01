package com.example.personmanagement.pensioner.model;

import com.example.personmanagement.employee.model.CreateEmployeeCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class PensionerConfiguration {

    private final ObjectMapper objectMapper;

    @PostConstruct
    public void objectMapper(){
        objectMapper.registerSubtypes(new NamedType(CreatePensionerCommand.class,"PENSIONER"));
    }


}
