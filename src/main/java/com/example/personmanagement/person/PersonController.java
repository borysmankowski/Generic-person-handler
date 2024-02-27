package com.example.personmanagement.person;

import com.example.personmanagement.person.model.CreatePersonCommand;
import com.example.personmanagement.person.model.PersonDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/people")
public class PersonController {

    private final PersonService personService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public PersonDto createPerson(@RequestBody @Valid CreatePersonCommand command) {
        return personService.create(command);

    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public Page<PersonDto> searchPersons(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String surname,
            @RequestParam(required = false) String pesel,
            @RequestParam(required = false) Double heightFrom,
            @RequestParam(required = false) Double heightTo,
            @RequestParam(required = false) Double weightFrom,
            @RequestParam(required = false) Double weightTo,
            @RequestParam(required = false) String emailAddress,
            @RequestParam(required = false) Double salaryFrom,
            @RequestParam(required = false) Double salaryTo,
            @RequestParam(required = false) String universityName,
            @RequestParam(required = false) Integer numberOfJobPositionsFrom,
            @RequestParam(required = false) Integer numberOfJobPositionsTo,
            Pageable pageable) {

        return personService.searchPersons(type, name, surname, pesel, heightFrom, heightTo,
                weightFrom, weightTo, emailAddress, salaryFrom, salaryTo, universityName,
                numberOfJobPositionsFrom, numberOfJobPositionsTo,
                pageable
        );
    }

    @PutMapping("/{personId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public PersonDto updatePersonDetails(@PathVariable Long personId, @RequestBody @Valid CreatePersonCommand command) {
        return personService.updateAnyPerson(personId, command);
    }

}