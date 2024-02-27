package com.example.personmanagement.employee.model.csvimport;

import com.example.personmanagement.employee.model.CreateEmployeeCommand;
import com.example.personmanagement.file.CsvFileRowToCreateCommandStrategy;
import com.example.personmanagement.person.model.CreatePersonCommand;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class EmployeeCsvFileRowToCreateCommandStrategy implements CsvFileRowToCreateCommandStrategy {
    @Override
    public CreatePersonCommand toCommand(String[] data) {
        if (!isSupported(data)) {
            throw new IllegalArgumentException("Not supported data!");
        }
        CreateEmployeeCommand command = new CreateEmployeeCommand();
        command.setType(data[0]);
        command.setName(data[1]);
        command.setSurname(data[2]);
        command.setPesel(data[3]);
        command.setHeight(Double.parseDouble(data[4]));
        command.setWeight(Double.parseDouble(data[5]));
        command.setEmailAddress(data[6]);
        command.setEmploymentStartDate(LocalDate.parse(data[7]));
        command.setCurrentPosition(data[8]);
        command.setCurrentSalary(Double.parseDouble(data[9]));
        return command;
    }

    @Override
    public boolean isSupported(String[] data) {
        String type = data[0];
        return type.equals("EMPLOYEE");
    }
}