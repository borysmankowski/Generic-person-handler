package com.example.personmanagement.config;

import com.example.personmanagement.model.employee.UpdateEmployeeCommand;
import com.example.personmanagement.model.pensioner.UpdatePensionerCommand;
import com.example.personmanagement.model.person.UpdatePersonCommand;
import com.example.personmanagement.model.student.UpdateStudentCommand;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UpdateCommandTypeRegistrar implements CommandTypeRegistrar<UpdatePersonCommand> {

    @Override
    public void registerCommandTypes(Map<String, Class<? extends UpdatePersonCommand>> commandTypeMap) {
        commandTypeMap.put("EMPLOYEE", UpdateEmployeeCommand.class);
        commandTypeMap.put("PENSIONER", UpdatePensionerCommand.class);
        commandTypeMap.put("STUDENT", UpdateStudentCommand.class);
    }
}
