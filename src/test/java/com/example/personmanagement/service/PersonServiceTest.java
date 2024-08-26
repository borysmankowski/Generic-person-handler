package com.example.personmanagement.service;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.exception.ResourceVersionNotValidException;
import com.example.personmanagement.mapper.PersonMapper;
import com.example.personmanagement.model.employee.Employee;
import com.example.personmanagement.model.person.CreatePersonCommand;
import com.example.personmanagement.model.person.Person;
import com.example.personmanagement.model.person.PersonDto;
import com.example.personmanagement.model.person.UpdatePersonCommand;
import com.example.personmanagement.repository.PersonRepository;
import com.example.personmanagement.search.PersonSpecification;
import com.example.personmanagement.search.SearchCriteria;
import com.example.personmanagement.strategy.EmployeeCreationStrategy;
import com.example.personmanagement.strategy.EmployeeUpdateStrategy;
import com.example.personmanagement.strategy.PersonStrategyFacade;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private PersonMapper personMapper;

    @Mock
    private EmployeeCreationStrategy employeeCreationStrategy;

    @Mock
    private PersonStrategyFacade personStrategyFacade;

    @Mock
    private EmployeeUpdateStrategy employeeUpdateStrategy;

    @Mock
    private PersonSpecification personSpecification;

    private PersonService personService;
    @Captor
    private ArgumentCaptor<Person> personArgumentCaptor;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        personService = new PersonService(personRepository, personMapper, personStrategyFacade, personSpecification, objectMapper);
    }

    @Test
    void create_ValidCommand_ReturnsEmployeeDto() {

        CreatePersonCommand command = new CreatePersonCommand();
        command.setType("EMPLOYEE");
        command.setName("Darek");
        command.setSurname("Pieczarek");
        command.setPesel("84040133386");
        command.setHeight(190);
        command.setWeight(90);
        command.setEmailAddress("test@test.com");

        Employee employee = new Employee();
        employee.setType("EMPLOYEE");
        employee.setName("Darek");
        employee.setSurname("Pieczarek");
        employee.setPesel("84040133386");
        employee.setHeight(190);
        employee.setWeight(90);
        employee.setEmailAddress("test@test.com");

        PersonDto personDto = new PersonDto();
        personDto.setId(1L);
        personDto.setName("Darek");
        personDto.setSurname("Pieczarek");
        personDto.setPesel("84040133386");
        personDto.setHeight(190);
        personDto.setWeight(90);

        personDto.setEmailAddress("test@test.com");

        when(personStrategyFacade.getCreationStrategy("EMPLOYEE")).thenReturn(employeeCreationStrategy);
        when(employeeCreationStrategy.create(any(CreatePersonCommand.class))).thenReturn(employee);
        when(personRepository.save(any(Employee.class))).thenReturn(employee);
        when(personMapper.toDto(any(Employee.class))).thenReturn(personDto);

        PersonDto result = personService.create(command);

        assertNotNull(result);
        assertEquals(personDto.getId(), result.getId());
        assertEquals(personDto.getName(), result.getName());
        assertEquals(personDto.getSurname(), result.getSurname());
        assertEquals(personDto.getPesel(), result.getPesel());
        assertEquals(personDto.getEmailAddress(), result.getEmailAddress());

        verify(personRepository, times(1)).save(personArgumentCaptor.capture());
        Person capturedPerson = personArgumentCaptor.getValue();
        assertEquals(employee.getType(), capturedPerson.getType());
        assertEquals(employee.getName(), capturedPerson.getName());
        assertEquals(employee.getSurname(), capturedPerson.getSurname());
        assertEquals(employee.getPesel(), capturedPerson.getPesel());
        assertEquals(employee.getHeight(), capturedPerson.getHeight(), 0.1);
        assertEquals(employee.getWeight(), capturedPerson.getWeight(), 0.1);
        assertEquals(employee.getEmailAddress(), capturedPerson.getEmailAddress());
    }


    @Test
    void create_InvalidStrategyType_ThrowsException() {
        CreatePersonCommand command = new CreatePersonCommand();
        command.setType("INVALID");

        when(personStrategyFacade.getCreationStrategy("INVALID")).thenThrow(new InvalidStrategyTypeException("Invalid strategy type"));

        assertThrows(InvalidStrategyTypeException.class, () -> {
            personService.create(command);
        });
        verify(personRepository, times(0)).save(any(Person.class));
    }

    @Test
    public void searchPersons_ShouldReturnPersonPage() throws JsonProcessingException {

        String searchCriteriaParam = "[{\"key\":\"name\",\"operation\":\"eq\",\"value\":\"John\",\"prefix\":\"null\"}]";

        Person person1 = new Person();
        person1.setId(2L);
        person1.setName("John");

        PersonDto personDto = new PersonDto();
        personDto.setId(2L);
        personDto.setName("John");

        Pageable pageable = PageRequest.of(0, 10);

        List<Person> personList = List.of(person1);
        Page<Person> personPage = new PageImpl<>(personList);

        List<SearchCriteria> searchCriteria = List.of(
                new SearchCriteria("name", "eq", "John", null)
        );

        when(objectMapper.readValue(eq(searchCriteriaParam), any(TypeReference.class)))
                .thenReturn(searchCriteria);

        Specification<Person> specification = Specification.where(null);
        when(personSpecification.addSpecification(any(Specification.class), any(SearchCriteria.class)))
                .thenReturn(specification);
        when(personRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(personPage);
        when(personMapper.toDto(any(Person.class))).thenReturn(personDto);

        Page<PersonDto> result = personService.searchPersons(searchCriteriaParam, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("John");
    }

    @Test
    public void searchPersons_ShouldReturnEmptyPages() throws JsonProcessingException {

        String searchCriteriaParam = "[]";

        Pageable pageable = PageRequest.of(0, 10);
        List<Person> personList = Collections.emptyList();
        Page<Person> personPage = new PageImpl<>(personList);

        when(objectMapper.readValue(eq(searchCriteriaParam), any(TypeReference.class)))
                .thenReturn(Collections.emptyList());

        when(personRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(personPage);

        Page<PersonDto> result = personService.searchPersons(searchCriteriaParam, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(0);
        verify(personRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void testUpdateAnyPerson_Success() {

        Employee existingEmployee = new Employee();
        existingEmployee.setId(1L);
        existingEmployee.setType("EMPLOYEE");
        existingEmployee.setName("Darek");
        existingEmployee.setSurname("Pieczarek");
        existingEmployee.setPesel("84040133386");
        existingEmployee.setHeight(190);
        existingEmployee.setWeight(90);
        existingEmployee.setEmailAddress("test@test.com");
        existingEmployee.setVersion(1);

        Employee updatedEmployee = new Employee();
        updatedEmployee.setId(1L);
        updatedEmployee.setType("EMPLOYEE");
        updatedEmployee.setName("New Name");
        updatedEmployee.setSurname("Pieczarek");
        updatedEmployee.setPesel("84040133386");
        updatedEmployee.setHeight(190);
        updatedEmployee.setWeight(90);
        updatedEmployee.setEmailAddress("test@test.com");
        updatedEmployee.setVersion(2);

        UpdatePersonCommand command = new UpdatePersonCommand();
        command.setType("EMPLOYEE");
        command.setName("New Name");
        command.setVersion(2);

        PersonDto expectedPersonDto = new PersonDto();
        expectedPersonDto.setName("New Name");

        when(personRepository.findById(existingEmployee.getId())).thenReturn(Optional.of(existingEmployee));
        when(personStrategyFacade.getUpdateStrategy("EMPLOYEE")).thenReturn(employeeUpdateStrategy);
        when(employeeUpdateStrategy.update(existingEmployee, command)).thenReturn(updatedEmployee);
        when(personRepository.save(updatedEmployee)).thenReturn(updatedEmployee);
        when(personMapper.toDto(updatedEmployee)).thenReturn(expectedPersonDto);

        PersonDto result = personService.updateAnyPerson(existingEmployee.getId(), command);

        assertEquals(expectedPersonDto, result);
        verify(personRepository, times(1)).findById(existingEmployee.getId());
        verify(personRepository, times(1)).save(updatedEmployee);
    }

    @Test
    public void testUpdateAnyPerson_OptimisticLockException() {

        Employee existingEmployee = new Employee();
        existingEmployee.setId(1L);
        existingEmployee.setType("EMPLOYEE");
        existingEmployee.setName("Darek");
        existingEmployee.setSurname("Pieczarek");
        existingEmployee.setPesel("84040133386");
        existingEmployee.setHeight(190);
        existingEmployee.setWeight(90);
        existingEmployee.setEmailAddress("test@test.com");
        existingEmployee.setVersion(1);

        UpdatePersonCommand command = new UpdatePersonCommand();
        command.setType("EMPLOYEE");
        command.setName("New Name");
        command.setVersion(2);

        when(personRepository.findById(existingEmployee.getId())).thenReturn(Optional.of(existingEmployee));
        when(personStrategyFacade.getUpdateStrategy("EMPLOYEE")).thenReturn(employeeUpdateStrategy);
        when(employeeUpdateStrategy.update(existingEmployee, command)).thenThrow(new ResourceVersionNotValidException("Optimistic lock failure"));

        assertThrows(ResourceVersionNotValidException.class, () -> {
            personService.updateAnyPerson(existingEmployee.getId(), command);
        });

        verify(personRepository, times(1)).findById(existingEmployee.getId());
        verify(personRepository, times(0)).save(any(Employee.class));
    }
}