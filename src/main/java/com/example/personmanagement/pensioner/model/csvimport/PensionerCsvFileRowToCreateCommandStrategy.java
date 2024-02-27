package com.example.personmanagement.pensioner.model.csvimport;

import com.example.personmanagement.file.CsvFileRowToCreateCommandStrategy;
import com.example.personmanagement.pensioner.model.CreatePensionerCommand;
import com.example.personmanagement.person.model.CreatePersonCommand;
import org.springframework.stereotype.Component;

@Component
public class PensionerCsvFileRowToCreateCommandStrategy implements CsvFileRowToCreateCommandStrategy {

    @Override
    public CreatePersonCommand toCommand(String[] data) {
        if (!isSupported(data)) {
            throw new IllegalArgumentException("Not supported data!");
        }
        CreatePensionerCommand command = new CreatePensionerCommand();
        command.setType(data[0]);
        command.setName(data[1]);
        command.setSurname(data[2]);
        command.setPesel(data[3]);
        command.setHeight(Double.parseDouble(data[4]));
        command.setWeight(Double.parseDouble(data[5]));
        command.setEmailAddress(data[6]);
        command.setPensionAmount(Double.parseDouble(data[7]));
        command.setWorkedYears(Integer.parseInt((data[8])));
        return command;
    }

    @Override
    public boolean isSupported(String[] data) {
        String type = data[0];
        return type.equals("PENSIONER");
    }
}
