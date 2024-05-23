package com.example.personmanagement.mapper.updateRegistrar;

import com.example.personmanagement.person.model.UpdatePersonCommand;
import com.example.personmanagement.student.model.UpdateStudentCommand;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component

public class StudentUpdateCommandTypeRegistrar implements UpdateCommandTypeRegistrar {

    @Override
    public void registerUpdateCommandTypes(Map<String, Class<? extends UpdatePersonCommand>> commandTypeMap) {
        commandTypeMap.put("STUDENT", UpdateStudentCommand.class);
    }
}