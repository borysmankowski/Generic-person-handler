package com.example.personmanagement.controller;

import com.example.personmanagement.model.person.CreatePersonCommand;
import com.example.personmanagement.model.person.PersonDto;
import com.example.personmanagement.model.person.UpdatePersonCommand;
import com.example.personmanagement.search.SearchCriteria;
import com.example.personmanagement.service.PersonService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/people")
public class PersonController {

    private final PersonService personService;
    private final ObjectMapper objectMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PersonDto> createPerson(@RequestBody @Valid CreatePersonCommand command) {
        PersonDto personCreated = personService.create(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(personCreated);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<PersonDto>> searchPersons(@RequestParam(value = "search-criteria", required = false) String searchCriteriaParam, Pageable pageable) throws JsonProcessingException {
        List<SearchCriteria> searchCriteria = objectMapper.readValue(searchCriteriaParam, new TypeReference<List<SearchCriteria>>() {
        });
        Page<PersonDto> personDtoPage = personService.searchPersons(searchCriteria, pageable);
        return ResponseEntity.ok(personDtoPage);
    }

    @PutMapping("/{personId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PersonDto> updatePersonDetails(@PathVariable Long personId, @RequestBody @Valid UpdatePersonCommand command) {
        PersonDto personDto = personService.updateAnyPerson(personId, command);
        return ResponseEntity.ok(personDto);

    }
}