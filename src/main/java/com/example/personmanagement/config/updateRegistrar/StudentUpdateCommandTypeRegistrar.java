package com.example.personmanagement.config.updateRegistrar;

import com.example.personmanagement.config.CommandTypeRegistrar;
import com.example.personmanagement.model.person.UpdatePersonCommand;
import com.example.personmanagement.model.student.UpdateStudentCommand;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component

public class StudentUpdateCommandTypeRegistrar implements CommandTypeRegistrar<UpdatePersonCommand> {

    @Override
    public void registerCommandTypes(Map<String, Class<? extends UpdatePersonCommand>> commandTypeMap) {
        commandTypeMap.put("STUDENT", UpdateStudentCommand.class);
    }
}
