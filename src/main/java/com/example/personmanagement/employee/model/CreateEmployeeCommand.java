package com.example.personmanagement.employee.model;

import com.example.personmanagement.person.model.CreatePersonCommand;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CreateEmployeeCommand extends CreatePersonCommand {

}