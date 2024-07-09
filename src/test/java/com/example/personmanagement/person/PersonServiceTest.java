package com.example.personmanagement.person;

import com.example.personmanagement.employee.EmployeeCreationStrategy;
import com.example.personmanagement.employee.EmployeeUpdateStrategy;
import com.example.personmanagement.employee.model.CreateEmployeeCommand;
import com.example.personmanagement.employee.model.Employee;
import com.example.personmanagement.employee.model.UpdateEmployeeCommand;
import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.mapper.PersonMapper;
import com.example.personmanagement.person.model.Person;
import com.example.personmanagement.person.model.PersonDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

    @InjectMocks
    private PersonService personService;

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
    void searchPersons() {
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

        UpdateEmployeeCommand command = new UpdateEmployeeCommand();
        command.setType("EMPLOYEE");
        command.setName("New Name");

        PersonDto expectedPersonDto = new PersonDto();
        expectedPersonDto.setName("New Name");


        when(personRepository.findById(existingEmployee.getId())).thenReturn(Optional.of(existingEmployee));
        when(employeeUpdateStrategy.update(existingEmployee, command)).thenReturn(existingEmployee);
        when(personRepository.save(existingEmployee)).thenReturn(existingEmployee);
        when(personMapper.toDto(existingEmployee)).thenReturn(expectedPersonDto);

        // when
        PersonDto result = personService.updateAnyPerson(existingEmployee.getId(), command);

        // then
        assertEquals(expectedPersonDto, result);
        verify(personRepository, times(1)).findById(existingEmployee.getId());
        verify(personRepository, times(1)).save(existingEmployee);
    }

}