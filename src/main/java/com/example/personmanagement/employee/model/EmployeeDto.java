package com.example.personmanagement.employee.model;

import com.example.personmanagement.person.model.PersonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class EmployeeDto extends PersonDto {

    private int numberOfJobPositions;
}
