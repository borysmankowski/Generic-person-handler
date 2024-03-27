package com.example.personmanagement.student.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class StudentConfiguration {

    private final ObjectMapper objectMapper;

    @PostConstruct
    public void objectMapper(){
        objectMapper.registerSubtypes(new NamedType(CreateStudentCommand.class,"STUDENT"));
        objectMapper.registerSubtypes(new NamedType(UpdateStudentCommand.class,"STUDENT"));
    }
}
