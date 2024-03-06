package com.example.personmanagement.person.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString(callSuper = true)
@Inheritance(strategy = InheritanceType.JOINED)
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
//    @Column(name = "TYPE")
    private String type;
//    @Column(name = "NAME")
    private String name;
//    @Column(name = "SURNAME")
    private String surname;
    @Column(unique = true)
    private String pesel;
//    @Column(name = "HEIGHT")
    private double height;
//    @Column(name = "WEIGHT")
    private double weight;
//    @Column(name = "EMAIL_ADDRESS")
    private String emailAddress;
    @Version
    private int version;

}