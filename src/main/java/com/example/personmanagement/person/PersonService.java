package com.example.personmanagement.person;

import com.example.personmanagement.exception.ResourceNotFoundException;
import com.example.personmanagement.exception.ResourceVersionNotValidException;
import com.example.personmanagement.mapper.PersonMapper;
import com.example.personmanagement.person.model.CreatePersonCommand;
import com.example.personmanagement.person.model.Person;
import com.example.personmanagement.person.model.PersonDto;
import com.example.personmanagement.person.model.PersonSpecification;
import com.example.personmanagement.person.model.PersonStrategyFacade;
import com.example.personmanagement.person.model.SearchCriteria;
import com.example.personmanagement.person.model.UpdatePersonCommand;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    private final PersonMapper personMapper;

    private final PersonValidator personValidator;

    private final PersonStrategyFacade personStrategyFacade;

    public PersonDto create(CreatePersonCommand command) {
        PersonCreationStrategy creationStrategy = personStrategyFacade.getCreationStrategy(command.getType());
        Person newPerson = creationStrategy.create(command);
        personValidator.validate(newPerson);
        return personMapper.toDto(personRepository.save(newPerson));
    }

    @Transactional(readOnly = true)
    public Page<PersonDto> searchPersons(List<SearchCriteria> searchCriteria, Pageable pageable) {
        Specification<Person> specification = PersonSpecification.any();

        for (SearchCriteria criteria : searchCriteria) {
            specification = PersonSpecification.addSpecification(specification, criteria);
        }

        Page<Person> result = personRepository.findAll(specification, pageable);
        return result.map(personMapper::toDto);
    }

    @Transactional
    public PersonDto updateAnyPerson(Long personId, UpdatePersonCommand command) {
        Person existingPerson = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with ID: " + personId));

        int commandVersion = Integer.parseInt(command.getVersion().substring(1)) - 1;

        if (existingPerson.getVersion() != commandVersion) {
            throw new ResourceVersionNotValidException("Person was modified during your update, please fetch the newest version and retry");
        }

        PersonUpdateStrategy updateStrategy = personStrategyFacade.getUpdateStrategy(command.getType());
        PersonDto personDto;

        try {
            Person updatedPerson = updateStrategy.update(existingPerson, command);
            personDto = personMapper.toDto(personRepository.save(updatedPerson));
        } catch (OptimisticLockException exception) {
            throw new ResourceVersionNotValidException("Person was modified during your update, please fetch the newest version and retry");
        }
        return personDto;
    }
}