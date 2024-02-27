package com.example.personmanagement.person;

import com.example.personmanagement.exception.ResourceNotFoundException;
import com.example.personmanagement.mapper.PersonMapper;
import com.example.personmanagement.person.model.CreatePersonCommand;
import com.example.personmanagement.person.model.Person;
import com.example.personmanagement.person.model.PersonDto;
import com.example.personmanagement.person.model.PersonSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PersonService {


    private final PersonRepository personRepository;

    private final PersonMapper personMapper;

    private final Map<String, PersonCreationStrategy> creationStrategies;


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
    public Page<PersonDto> searchPersons(String type, String name, String surname, String pesel,
                                         Double heightFrom, Double heightTo, Double weightFrom, Double weightTo,
                                         String emailAddress, Double salaryFrom, Double salaryTo, String universityName,
                                         Integer numberOfJobPositionsFrom, Integer numberOfJobPositionsTo,
                                         Pageable pageable) {

        Specification<Person> specification = Specification.where(PersonSpecification.any());

        if (type != null) {
            specification = specification.and(PersonSpecification.typeEquals(type));
        }
        if (name != null) {
            specification = specification.and(PersonSpecification.nameContainsIgnoreCase(name));
        }
        if (surname != null) {
            specification = specification.and(PersonSpecification.surnameContainsIgnoreCase(surname));
        }
        if (pesel != null) {
            specification = specification.and(PersonSpecification.peselEquals(pesel));
        }
        if (heightFrom != null || heightTo != null) {
            specification = specification.and(PersonSpecification.heightBetween(
                    heightFrom == null ? Double.MIN_VALUE : heightFrom,
                    heightTo == null ? Double.MAX_VALUE : heightTo));
        }
        if (weightFrom != null || weightTo != null) {
            specification = specification.and(PersonSpecification.weightBetween(
                    weightFrom == null ? Double.MIN_VALUE : weightFrom,
                    weightTo == null ? Double.MAX_VALUE : weightTo));
        }
        if (emailAddress != null) {
            specification = specification.and(PersonSpecification.emailAddressContainsIgnoreCase(emailAddress));
        }
        if (salaryFrom != null || salaryTo != null) {
            specification = specification.and(PersonSpecification.salaryBetween(
                    salaryFrom == null ? Double.MIN_VALUE : salaryFrom,
                    salaryTo == null ? Double.MAX_VALUE : salaryTo));
        }
        if (universityName != null) {
            specification = specification.and(PersonSpecification.universityNameEquals(universityName));
        }
        if (numberOfJobPositionsFrom != null || numberOfJobPositionsTo != null) {
            specification = specification.and(PersonSpecification.numberOfJobPositionsBetween(
                    numberOfJobPositionsFrom == null ? Integer.MIN_VALUE : numberOfJobPositionsFrom,
                    numberOfJobPositionsTo == null ? Integer.MAX_VALUE : numberOfJobPositionsTo));
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