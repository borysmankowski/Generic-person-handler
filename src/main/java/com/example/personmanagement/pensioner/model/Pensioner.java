package com.example.personmanagement.pensioner.model;

import com.example.personmanagement.person.model.Person;
import jakarta.persistence.Entity;
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
@Entity
@ToString(callSuper = true)
public class Pensioner extends Person {

    private double pensionAmount;
    private int workedYears;

}
