package com.example.personmanagement.person;

import com.example.personmanagement.employee.EmployeeCreationStrategy;
import com.example.personmanagement.employee.EmployeeUpdateStrategy;
import com.example.personmanagement.employee.model.CreateEmployeeCommand;
import com.example.personmanagement.employee.model.Employee;
import com.example.personmanagement.employee.model.UpdateEmployeeCommand;
import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.exception.ResourceNotFoundException;
import com.example.personmanagement.exception.ResourceVersionNotValidException;
import com.example.personmanagement.mapper.PersonMapper;
import com.example.personmanagement.person.model.Person;
import com.example.personmanagement.person.model.PersonDto;
import com.example.personmanagement.person.model.SearchCriteria;
import com.example.personmanagement.person.model.UpdatePersonCommand;
import jakarta.persistence.OptimisticLockException;
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
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private PersonMapper personMapper;

    @Mock
    private PersonValidator personValidator;

    @Mock
    private Map<String, PersonCreationStrategy> personCreationStrategy;

    @Mock
    private EmployeeCreationStrategy employeeCreationStrategy;

    @Mock
    private EmployeeUpdateStrategy employeeUpdateStrategy;

    @Mock
    private Map<String, PersonUpdateStrategy> personUpdateStrategy;

    private PersonService personService;

    @BeforeEach
    void setUp(){personService = new PersonService(personRepository, personMapper, personValidator, personCreationStrategy, personUpdateStrategy);
    }

    @Captor
    private ArgumentCaptor<Person> personArgumentCaptor;

    @Test
    void create_ValidCommand_ReturnsEmployeeDto() {

        // Given
        CreateEmployeeCommand command = new CreateEmployeeCommand();
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

        when(personCreationStrategy.get("employeeCreationStrategy")).thenReturn(employeeCreationStrategy);
        when(employeeCreationStrategy.create(any(CreateEmployeeCommand.class))).thenReturn(employee);
        when(personRepository.save(any(Employee.class))).thenReturn(employee);
        when(personMapper.toDto(any(Employee.class))).thenReturn(personDto);

        // When
        PersonDto result = personService.create(command);

        // Then
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

        CreateEmployeeCommand command = new CreateEmployeeCommand();
        command.setType("INVALID");
        command.setName("Darek");
        command.setSurname("Pieczarek");
        command.setPesel("84040133386");
        command.setHeight(190);
        command.setWeight(90);
        command.setEmailAddress("test@test.com");

        assertThrows(InvalidStrategyTypeException.class, () -> {
            personService.create(command);
        });

        verify(personRepository, times(0)).save(any(Person.class));
    }

    @Test
    public void searchPersons_ShouldReturnPersonPage() {

        Employee person1= new Employee();
        person1.setId(2L);
        person1.setName("John");

        PersonDto personDto = new PersonDto();
        personDto.setId(1L);
        personDto.setName("John");

        Pageable pageable = PageRequest.of(0, 10);

        List<SearchCriteria> searchCriteria = Collections.singletonList(
                new SearchCriteria("name", "eq", "John", null)
        );
        List<Person> personList = List.of(person1);
        Page<Person> personPage = new PageImpl<>(personList);

        when(personRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(personPage);
        when(personMapper.toDto(any(Person.class))).thenReturn(personDto);

        Page<PersonDto> result = personService.searchPersons(searchCriteria, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("John");

    }

    @Test
    public void searchPersons_ShouldReturnEmptyPage() {
        List<SearchCriteria> criteria = new ArrayList<>();

        Pageable pageable = PageRequest.of(0, 10);
        List<Person> personList = Collections.emptyList();
        Page<Person> personPage = new PageImpl<>(personList);

        when(personRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(personPage);

        Page<PersonDto> result = personService.searchPersons(criteria, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(0);
        verify(personRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void testUpdateAnyPerson_Success() {
        // given
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

        UpdateEmployeeCommand command = new UpdateEmployeeCommand();
        command.setType("EMPLOYEE");
        command.setName("New Name");

        PersonDto expectedPersonDto = new PersonDto();
        expectedPersonDto.setName("New Name");

        when(personUpdateStrategy.get("employeeUpdateStrategy")).thenReturn(employeeUpdateStrategy);
        when(personRepository.findById(existingEmployee.getId())).thenReturn(Optional.of(existingEmployee));
        when(employeeUpdateStrategy.update(existingEmployee, command)).thenAnswer(invocation -> {
            Employee employee = invocation.getArgument(0);
            employee.setName(command.getName());
            employee.setVersion(employee.getVersion() + 1);
            return employee;
        });
        when(personRepository.save(any(Employee.class))).thenReturn(updatedEmployee);
        when(personMapper.toDto(any(Employee.class))).thenReturn(expectedPersonDto);

        // when
        PersonDto result = personService.updateAnyPerson(existingEmployee.getId(), command);

        // then
        assertEquals(expectedPersonDto, result);

        ArgumentCaptor<Employee> employeeCaptor = ArgumentCaptor.forClass(Employee.class);
        verify(personRepository).save(employeeCaptor.capture());
        Employee savedEmployee = employeeCaptor.getValue();

        assertEquals(2, savedEmployee.getVersion());

        verify(personRepository, times(1)).findById(existingEmployee.getId());
        verify(personRepository, times(1)).save(savedEmployee);
        verifyNoMoreInteractions(personRepository);
    }

    @Test
    void testUpdateAnyPerson_NotFound() {
        // given
        Long personId = 1L;
        UpdatePersonCommand command = new UpdatePersonCommand();
        command.setType("EMPLOYEE");

        when(personRepository.findById(personId)).thenReturn(Optional.empty());
        // when
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            personService.updateAnyPerson(personId, command);
        });
        // then
        assertEquals("Person not found with ID: 1", exception.getMessage());
        verify(personRepository, times(1)).findById(personId);
        verifyNoMoreInteractions(personRepository);
    }

    @Test
    void testUpdateAnyPerson_OptimisticLockException() {
        // given
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

        when(personRepository.findById(existingEmployee.getId())).thenReturn(Optional.of(existingEmployee));
        when(employeeUpdateStrategy.update(existingEmployee, command)).thenThrow(new OptimisticLockException());

        when(personUpdateStrategy.get("employeeUpdateStrategy")).thenReturn(employeeUpdateStrategy);

        // when
        ResourceVersionNotValidException exception = assertThrows(ResourceVersionNotValidException.class, () -> {
            personService.updateAnyPerson(existingEmployee.getId(), command);
        });

        // then
        assertEquals("Person was modified during your update, please fetch the newest version and retry", exception.getMessage());
        verify(personRepository, times(1)).findById(existingEmployee.getId());
        verify(employeeUpdateStrategy, times(1)).update(existingEmployee, command);
        verifyNoMoreInteractions(personRepository, employeeUpdateStrategy);
    }
}