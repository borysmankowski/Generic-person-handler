package com.example.personmanagement.person.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.pl.PESEL;

@Data
public class UpdatePersonCommand {

    private String type;
    @NotBlank(message = "Name cannot be blank")
    private String name;
    @NotBlank(message = "Surname cannot be blank")
    private String surname;
    @PESEL
    private String pesel;
    private double height;
    private double weight;
    @Email
    private String emailAddress;
    private String version;
}