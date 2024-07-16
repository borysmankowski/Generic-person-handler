package com.example.personmanagement.model.pensioner;

import com.example.personmanagement.model.person.UpdatePersonCommand;
import lombok.Data;

@Data
public class UpdatePensionerCommand extends UpdatePersonCommand {

    private double pensionAmount;
    private int workedYears;
}
