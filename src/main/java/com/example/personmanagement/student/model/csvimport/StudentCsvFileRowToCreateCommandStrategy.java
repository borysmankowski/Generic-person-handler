package com.example.personmanagement.student.model.csvimport;

import com.example.personmanagement.file.CsvFileRowToCreateCommandStrategy;
import com.example.personmanagement.person.model.CreatePersonCommand;
import com.example.personmanagement.student.model.CreateStudentCommand;
import org.springframework.stereotype.Component;

@Component
public class StudentCsvFileRowToCreateCommandStrategy implements CsvFileRowToCreateCommandStrategy {

    @Override
    public CreatePersonCommand toCommand(String[] data) {
        if (!isSupported(data)) {
            throw new IllegalArgumentException("Not supported data!");
        }
        CreateStudentCommand command = new CreateStudentCommand();
        command.setType(data[0]);
        command.setName(data[1]);
        command.setSurname(data[2]);
        command.setPesel(data[3]);
        command.setHeight(Double.parseDouble(data[4]));
        command.setWeight(Double.parseDouble(data[5]));
        command.setEmailAddress(data[6]);
        command.setNameOfUniversity(data[7]);
        command.setYearOfStudies(Integer.parseInt(data[8]));
        command.setCourseName((data[9]));
        command.setScholarship(Double.parseDouble(data[10]));
        return command;
    }

    @Override
    public boolean isSupported(String[] data) {
        var type = data[0];
        return type.equals("STUDENT");
    }
}
