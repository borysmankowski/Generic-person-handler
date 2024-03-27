package com.example.personmanagement.pensioner.model;

import com.example.personmanagement.person.model.CreatePersonCommand;
import lombok.Data;


@Data
public class CreatePensionerCommand extends CreatePersonCommand {

    private double pensionAmount;
    private int workedYears;
}

