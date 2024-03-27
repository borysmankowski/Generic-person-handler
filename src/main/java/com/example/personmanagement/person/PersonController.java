package com.example.personmanagement.person;

import com.example.personmanagement.person.model.CreatePersonCommand;
import com.example.personmanagement.person.model.PersonDto;
import com.example.personmanagement.person.model.SearchCriteria;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @PostMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Page<PersonDto> searchPersons(@RequestBody List<SearchCriteria> searchCriteria, Pageable pageable) {
        return personService.searchPersons(searchCriteria, pageable);
    }

    @PutMapping("/{personId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public PersonDto updatePersonDetails(@PathVariable Long personId, @RequestBody @Valid CreatePersonCommand command) {
        return personService.updateAnyPerson(personId, command);
    }

}