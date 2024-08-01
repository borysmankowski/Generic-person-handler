package com.example.personmanagement.config.createRegistrar;

import com.example.personmanagement.config.CommandTypeRegistrar;
import com.example.personmanagement.model.person.CreatePersonCommand;
import com.example.personmanagement.model.student.CreateStudentCommand;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class StudentCreateCommandTypeRegistrar implements CommandTypeRegistrar<CreatePersonCommand> {
    @Override
    public void registerCommandTypes(Map<String, Class<? extends CreatePersonCommand>> commandTypeMap) {
        commandTypeMap.put("STUDENT", CreateStudentCommand.class);
    }
}
