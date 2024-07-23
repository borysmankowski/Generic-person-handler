package com.example.personmanagement.model.pensioner;

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
public class PensionerDto extends PersonDto {

    private double pensionAmount;
    private int workedYears;
}
