package com.example.personmanagement.person;

import com.example.personmanagement.exception.ResourceNotFoundException;
import com.example.personmanagement.mapper.PersonMapper;
import com.example.personmanagement.person.model.CreatePersonCommand;
import com.example.personmanagement.person.model.Person;
import com.example.personmanagement.person.model.PersonDto;
import com.example.personmanagement.person.model.PersonSpecification;
import com.example.personmanagement.person.model.SearchCriteria;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PersonService {


    private final PersonRepository personRepository;

    private final PersonMapper personMapper;

    private final Map<String, PersonCreationStrategy> creationStrategies;

    private final PersonSpecification personSpecification;


    public PersonDto create(CreatePersonCommand command) {
        String type = command.getType();
        PersonCreationStrategy creationStrategy = creationStrategies.get(type);

        if (creationStrategy == null) {
            throw new ResourceNotFoundException("Missing strategy type: " + type);
        }
        Person newPerson = creationStrategy.create(command);
        log.info("created: {}", newPerson);
        return personMapper.toDto(personRepository.save(newPerson));
    }

    @Transactional(readOnly = true)
    public Page<PersonDto> searchPersons(List<SearchCriteria> searchCriteria, Pageable pageable) {
        Specification<Person> specification = PersonSpecification.any();

        for (SearchCriteria criteria : searchCriteria) {
            specification = personSpecification.addSpecification(specification, criteria);
        }

        Page<Person> result = personRepository.findAll(specification, pageable);
        return result.map(personMapper::toDto);
    }




    @Transactional
    public PersonDto updateAnyPerson(Long personId, CreatePersonCommand command) {
        Person existingPerson = personRepository.findPersonByIdWithLock(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with ID: " + personId));

        String type = command.getType();
        PersonCreationStrategy creationStrategy = creationStrategies.get(type);

        Person updatedPerson = creationStrategy.create(command);
        updatedPerson.setId(existingPerson.getId());

        log.info("updated: {}", updatedPerson);
        return personMapper.toDto(personRepository.save(updatedPerson));
    }


}