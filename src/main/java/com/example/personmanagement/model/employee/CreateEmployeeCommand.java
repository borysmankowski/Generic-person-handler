package com.example.personmanagement.model.employee;

import com.example.personmanagement.model.person.CreatePersonCommand;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CreateEmployeeCommand extends CreatePersonCommand {

}