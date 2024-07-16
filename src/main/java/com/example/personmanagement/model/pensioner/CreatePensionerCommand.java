package com.example.personmanagement.model.pensioner;

import com.example.personmanagement.model.person.CreatePersonCommand;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CreatePensionerCommand extends CreatePersonCommand {

    private double pensionAmount;
    private int workedYears;
}