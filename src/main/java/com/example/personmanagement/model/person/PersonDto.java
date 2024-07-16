package com.example.personmanagement.model.person;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@NoArgsConstructor
@AllArgsConstructor
@Getter
@SuperBuilder
public class PersonDto {

    private Long id;
    private String name;
    private String surname;
    private String pesel;
    private double height;
    private double weight;
    private String emailAddress;
}