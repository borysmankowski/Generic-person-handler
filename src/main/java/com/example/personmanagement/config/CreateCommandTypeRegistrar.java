package com.example.personmanagement.config;

import com.example.personmanagement.model.employee.CreateEmployeeCommand;
import com.example.personmanagement.model.pensioner.CreatePensionerCommand;
import com.example.personmanagement.model.person.CreatePersonCommand;
import com.example.personmanagement.model.student.CreateStudentCommand;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CreateCommandTypeRegistrar implements CommandTypeRegistrar<CreatePersonCommand> {

    @Override
    public void registerCommandTypes(Map<String, Class<? extends CreatePersonCommand>> commandTypeMap) {
        commandTypeMap.put("EMPLOYEE", CreateEmployeeCommand.class);
        commandTypeMap.put("PENSIONER", CreatePensionerCommand.class);
        commandTypeMap.put("STUDENT", CreateStudentCommand.class);
    }
}
