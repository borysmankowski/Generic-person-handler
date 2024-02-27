package com.example.personmanagement.employee.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public class JobPositionResponseBody {

    private final String message;

    private final Long id;
}
