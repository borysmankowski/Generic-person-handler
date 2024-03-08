package com.example.personmanagement.person.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchCriteria {

    private String key;
    private String operation;
    private Object value;
    private Object secondValue;
}