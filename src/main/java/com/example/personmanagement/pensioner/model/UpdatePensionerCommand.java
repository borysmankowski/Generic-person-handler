package com.example.personmanagement.pensioner.model;

import com.example.personmanagement.person.model.UpdatePersonCommand;
import lombok.Data;

@Data
public class UpdatePensionerCommand extends UpdatePersonCommand {

    private double pensionAmount;
    private int workedYears;
}
