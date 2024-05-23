package com.example.personmanagement.employee.position;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class JobPositionResponseBody {

    private final String message;

    private final Long id;
}