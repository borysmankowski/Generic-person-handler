package com.example.personmanagement.model.employee;

import com.example.personmanagement.model.person.PersonDto;
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

    private String currentJobPosition;
}
