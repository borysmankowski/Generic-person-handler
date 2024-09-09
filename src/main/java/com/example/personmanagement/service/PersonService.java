package com.example.personmanagement.service;

import com.example.personmanagement.exception.ResourceNotFoundException;
import com.example.personmanagement.exception.ResourceVersionNotValidException;
import com.example.personmanagement.mapper.PersonMapper;
import com.example.personmanagement.model.person.CreatePersonCommand;
import com.example.personmanagement.model.person.Person;
import com.example.personmanagement.model.person.PersonDto;
import com.example.personmanagement.model.person.UpdatePersonCommand;
import com.example.personmanagement.repository.PersonRepository;
import com.example.personmanagement.search.PersonSpecification;
import com.example.personmanagement.search.SearchCriteria;
import com.example.personmanagement.strategy.PersonCreationStrategy;
import com.example.personmanagement.strategy.PersonStrategyFacade;
import com.example.personmanagement.strategy.PersonUpdateStrategy;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;
    private final PersonMapper personMapper;
    private final PersonStrategyFacade personStrategyFacade;
    private final PersonSpecification personSpecification;
    private final ObjectMapper objectMapper;

    public PersonDto create(CreatePersonCommand command) {
        PersonCreationStrategy creationStrategy = personStrategyFacade.getCreationStrategy(command.getType());
        Person newPerson = creationStrategy.create(command);
        return personMapper.toDto(personRepository.save(newPerson));
    }

    @Transactional(readOnly = true)
    public Page<PersonDto> searchPersons(String searchCriteriaParam, Pageable pageable) throws JsonProcessingException {
        List<SearchCriteria> searchCriteria = objectMapper.readValue(searchCriteriaParam, new TypeReference<List<SearchCriteria>>() {
        });

        Specification<Person> specification = PersonSpecification.any();
        for (SearchCriteria criteria : searchCriteria) {
            specification = personSpecification.addSpecification(specification, criteria);
        }

        Page<Person> result = personRepository.findAll(specification, pageable);
        return result.map(personMapper::toDto);
    }

    @Transactional
    public PersonDto updateAnyPerson(Long personId, UpdatePersonCommand command) {

        PersonUpdateStrategy updateStrategy = personStrategyFacade.getUpdateStrategy(command.getType());
        PersonDto personDto;

        try {
            Person updatedPerson = updateStrategy.update(personId, command);
            personDto = personMapper.toDto(personRepository.save(updatedPerson));
        } catch (ObjectOptimisticLockingFailureException exception) {
            throw new ResourceVersionNotValidException("Person was modified during your update, please fetch the newest version and retry");
        }
        return personDto;
    }
}