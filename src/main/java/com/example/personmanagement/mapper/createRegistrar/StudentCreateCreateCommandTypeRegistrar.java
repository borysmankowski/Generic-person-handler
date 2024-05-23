package com.example.personmanagement.mapper.createRegistrar;

import com.example.personmanagement.person.model.CreatePersonCommand;
import com.example.personmanagement.student.model.CreateStudentCommand;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class StudentCreateCreateCommandTypeRegistrar implements CreateCommandTypeRegistrar {

    @Override
    public void registerCreateCommandTypes(Map<String, Class<? extends CreatePersonCommand>> commandTypeMap) {
        commandTypeMap.put("STUDENT", CreateStudentCommand.class);
    }
}