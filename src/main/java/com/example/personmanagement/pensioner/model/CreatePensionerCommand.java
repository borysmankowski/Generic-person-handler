package com.example.personmanagement.pensioner.model;

import com.example.personmanagement.employee.model.CreateEmployeeCommand;
import com.example.personmanagement.person.model.CreatePersonCommand;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import lombok.Data;


@Data
public class CreatePensionerCommand extends CreatePersonCommand {

    private double pensionAmount;
    private int workedYears;
}

