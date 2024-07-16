package com.example.personmanagement.person;

import com.example.personmanagement.employee.model.CreateEmployeeCommand;
import com.example.personmanagement.employee.model.Employee;
import com.example.personmanagement.employee.model.EmployeeDto;
import com.example.personmanagement.employee.model.UpdateEmployeeCommand;
import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.exception.ResourceNotFoundException;
import com.example.personmanagement.mapper.PersonMapper;
import com.example.personmanagement.pensioner.model.CreatePensionerCommand;
import com.example.personmanagement.pensioner.model.PensionerDto;
import com.example.personmanagement.person.model.Person;
import com.example.personmanagement.person.model.PersonDto;
import com.example.personmanagement.person.model.PersonStrategyFacade;
import com.example.personmanagement.person.model.SearchCriteria;
import com.example.personmanagement.person.model.UpdatePersonCommand;
import com.example.personmanagement.student.model.CreateStudentCommand;
import com.example.personmanagement.student.model.Student;
import com.example.personmanagement.student.model.StudentDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
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


@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class PersonServiceTest {

    @SpyBean
    private PersonRepository personRepository;

    @Autowired
    private PersonMapper personMapper;

    @Mock
    private PersonValidator personValidator;

    @Autowired
    private Map<String, PersonCreationStrategy> personCreationStrategy;

    @Autowired
    private Map<String, PersonUpdateStrategy> personUpdateStrategy;

    private PersonService personService;
    @Captor
    private ArgumentCaptor<Person> personArgumentCaptor;

    @BeforeEach
    void setUp() {
        personRepository.deleteAll();
        PersonStrategyFacade personStrategyFacade = new PersonStrategyFacade(personCreationStrategy, personUpdateStrategy);
        personService = new PersonService(personRepository, personMapper, personValidator, personStrategyFacade);
    }

    @Test
    void create_ValidEmployeeCommand_ReturnsEmployeeDto() {
        // Given
        CreateEmployeeCommand command = new CreateEmployeeCommand();
        command.setType("EMPLOYEE");
        command.setName("John");
        command.setSurname("Doe");
        command.setPesel("1234567890");
        command.setHeight(180.0);
        command.setWeight(75.0);
        command.setEmailAddress("john.doe@example.com");

        // When
        EmployeeDto result = (EmployeeDto) personService.create(command);

        // Then
        assertNotNull(result);
        assertEquals(command.getName(), result.getName());
        assertEquals(command.getSurname(), result.getSurname());
        assertEquals(command.getPesel(), result.getPesel());
        assertEquals(command.getHeight(), result.getHeight(), 0.1);
        assertEquals(command.getWeight(), result.getWeight(), 0.1);
        assertEquals(command.getEmailAddress(), result.getEmailAddress());

        verify(personRepository).save(personArgumentCaptor.capture());

        Person capturedPerson = personArgumentCaptor.getValue();
        assertNotNull(capturedPerson);
        assertEquals(command.getName(), capturedPerson.getName());
        assertEquals(command.getSurname(), capturedPerson.getSurname());
        assertEquals(command.getPesel(), capturedPerson.getPesel());
        assertEquals(command.getHeight(), capturedPerson.getHeight(), 0.1);
        assertEquals(command.getWeight(), capturedPerson.getWeight(), 0.1);
        assertEquals(command.getEmailAddress(), capturedPerson.getEmailAddress());
    }

    @Test
    void create_ValidStudentCommand_ReturnsStudentDto() {
        // Given
        CreateStudentCommand command = new CreateStudentCommand();
        command.setType("STUDENT");
        command.setName("Alice");
        command.setSurname("Smith");
        command.setPesel("9876543210");
        command.setHeight(165.0);
        command.setWeight(55.0);
        command.setEmailAddress("alice.smith@example.com");
        command.setNameOfUniversity("University of Example");
        command.setYearOfStudies(2);
        command.setCourseName("Computer Science");
        command.setScholarship(1500.0);

        // When
        StudentDto result = (StudentDto) personService.create(command);

        // Then
        assertNotNull(result);
        assertEquals(command.getName(), result.getName());
        assertEquals(command.getSurname(), result.getSurname());
        assertEquals(command.getPesel(), result.getPesel());
        assertEquals(command.getHeight(), result.getHeight(), 0.1);
        assertEquals(command.getWeight(), result.getWeight(), 0.1);
        assertEquals(command.getEmailAddress(), result.getEmailAddress());
        assertEquals(command.getNameOfUniversity(), result.getNameOfUniversity());
        assertEquals(command.getYearOfStudies(), result.getYearOfStudies());
        assertEquals(command.getCourseName(), result.getCourseName());
        assertEquals(command.getScholarship(), result.getScholarship(), 0.1);

        verify(personRepository).save(personArgumentCaptor.capture());

        Person capturedPerson = personArgumentCaptor.getValue();
        assertNotNull(capturedPerson);
        assertEquals(command.getName(), capturedPerson.getName());
        assertEquals(command.getSurname(), capturedPerson.getSurname());
        assertEquals(command.getPesel(), capturedPerson.getPesel());
        assertEquals(command.getHeight(), capturedPerson.getHeight(), 0.1);
        assertEquals(command.getWeight(), capturedPerson.getWeight(), 0.1);
        assertEquals(command.getEmailAddress(), capturedPerson.getEmailAddress());
    }

    @Test
    void create_ValidPensionerCommand_ReturnsPensionerDto() {
        // Given
        CreatePensionerCommand command = new CreatePensionerCommand();
        command.setType("PENSIONER");
        command.setName("Michael");
        command.setSurname("Johnson");
        command.setPesel("5678901234");
        command.setHeight(170.0);
        command.setWeight(80.0);
        command.setEmailAddress("michael.johnson@example.com");
        command.setPensionAmount(2500.0);
        command.setWorkedYears(35);

        // When
        PensionerDto result = (PensionerDto) personService.create(command);

        // Then
        assertNotNull(result);
        assertEquals(command.getName(), result.getName());
        assertEquals(command.getSurname(), result.getSurname());
        assertEquals(command.getPesel(), result.getPesel());
        assertEquals(command.getHeight(), result.getHeight(), 0.1);
        assertEquals(command.getWeight(), result.getWeight(), 0.1);
        assertEquals(command.getEmailAddress(), result.getEmailAddress());
        assertEquals(command.getPensionAmount(), result.getPensionAmount(), 0.1);
        assertEquals(command.getWorkedYears(), result.getWorkedYears());

        verify(personRepository).save(personArgumentCaptor.capture());

        Person capturedPerson = personArgumentCaptor.getValue();
        assertNotNull(capturedPerson);
        assertEquals(command.getName(), capturedPerson.getName());
        assertEquals(command.getSurname(), capturedPerson.getSurname());
        assertEquals(command.getPesel(), capturedPerson.getPesel());
        assertEquals(command.getHeight(), capturedPerson.getHeight(), 0.1);
        assertEquals(command.getWeight(), capturedPerson.getWeight(), 0.1);
        assertEquals(command.getEmailAddress(), capturedPerson.getEmailAddress());
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
    public void searchPersons_ShouldReturnStudentPage() {

        Student student1 = new Student();
        student1.setType("STUDENT");
        student1.setName("John");
        student1.setSurname("Doe");
        student1.setPesel("1234567890");
        student1.setHeight(180.0);
        student1.setWeight(75.0);
        student1.setEmailAddress("john.doe@example.com");
        student1.setCourseName("Inzyniernia");
        student1.setScholarship(4000);
        student1.setNameOfUniversity("Politechnika Poznanska");

        personRepository.save(student1);

        Student student2 = new Student();
        student2.setType("STUDENT");
        student2.setName("Darek");
        student2.setSurname("Doe");
        student2.setPesel("1234566890");
        student2.setHeight(180.0);
        student2.setWeight(75.0);
        student2.setEmailAddress("john.doe@example.com");
        student2.setCourseName("Inzyniernia");
        student2.setScholarship(4000);
        student2.setNameOfUniversity("Politechnika Poznanska");

        personRepository.save(student2);

        // Search criteria
        List<SearchCriteria> searchCriteria = List.of(
                new SearchCriteria("name", "eq", "John", null)
        );

        Pageable pageable = PageRequest.of(0, 10);

        // Perform search
        Page<PersonDto> result = personService.searchPersons(searchCriteria, pageable);

        // Assertions
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("John");
    }

    @Test
    public void searchPersons_ShouldReturnAllStudents() {

        Student student1 = new Student();
        student1.setType("STUDENT");
        student1.setName("John");
        student1.setSurname("Doe");
        student1.setPesel("1234567890");
        student1.setHeight(180.0);
        student1.setWeight(75.0);
        student1.setEmailAddress("john.doe@example.com");
        student1.setCourseName("Inzyniernia");
        student1.setScholarship(4000);
        student1.setNameOfUniversity("Politechnika Poznanska");

        personRepository.save(student1);

        Student student2 = new Student();
        student2.setType("STUDENT");
        student2.setName("Darek");
        student2.setSurname("Doe");
        student2.setPesel("1234566890");
        student2.setHeight(180.0);
        student2.setWeight(75.0);
        student2.setEmailAddress("john.doe@example.com");
        student2.setCourseName("Inzyniernia");
        student2.setScholarship(4000);
        student2.setNameOfUniversity("Politechnika Poznanska");

        personRepository.save(student2);


        Pageable pageable = PageRequest.of(0, 10);

        // Perform search
        Page<PersonDto> result = personService.searchPersons(new ArrayList<>(), pageable);

        // Assertions
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().get(0).getName()).isEqualTo("John");
        assertThat(result.getContent().get(1).getName()).isEqualTo("Darek");
    }

    @Test
    public void searchPersons_ShouldReturnEmptyPage() {

        Student student1 = new Student();
        student1.setType("STUDENT");
        student1.setName("John");
        student1.setSurname("Doe");
        student1.setPesel("1234567890");
        student1.setHeight(180.0);
        student1.setWeight(75.0);
        student1.setEmailAddress("john.doe@example.com");
        student1.setCourseName("Inzyniernia");
        student1.setScholarship(4000);
        student1.setNameOfUniversity("Politechnika Poznanska");

        personRepository.save(student1);

        Student student2 = new Student();
        student2.setType("STUDENT");
        student2.setName("Darek");
        student2.setSurname("Doe");
        student2.setPesel("1234566890");
        student2.setHeight(180.0);
        student2.setWeight(75.0);
        student2.setEmailAddress("john.doe@example.com");
        student2.setCourseName("Inzyniernia");
        student2.setScholarship(4000);
        student2.setNameOfUniversity("Politechnika Poznanska");

        personRepository.save(student2);

        // Search criteria
        List<SearchCriteria> searchCriteria = List.of(
                new SearchCriteria("name", "eq", "Michal", null)
        );

        Pageable pageable = PageRequest.of(0, 10);

        // Perform search
        Page<PersonDto> result = personService.searchPersons(searchCriteria, pageable);

        // Assertions
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    void testUpdateAnyPerson_Success_ShouldIncrementVersion() {
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
        existingEmployee.setVersion(0);

        UpdateEmployeeCommand command = new UpdateEmployeeCommand();
        command.setType("EMPLOYEE");
        command.setName("NOWE IMIE");
        command.setVersion("v1");

        personRepository.save(existingEmployee);

        // when
        PersonDto result = personService.updateAnyPerson(existingEmployee.getId(), command);

        // then
        assertNotNull(result);
        assertEquals(command.getName(), result.getName());
        assertEquals(Optional.of(existingEmployee.getId()), Optional.of(result.getId()));
        assertEquals(existingEmployee.getSurname(), result.getSurname());
        assertEquals(existingEmployee.getPesel(), result.getPesel());
        assertEquals(existingEmployee.getHeight(), result.getHeight(), 0.1);
        assertEquals(existingEmployee.getWeight(), result.getWeight(), 0.1);
        assertEquals(existingEmployee.getEmailAddress(), result.getEmailAddress());
        assertThat(existingEmployee.getVersion() == 1);

        verify(personRepository, times(1)).save(existingEmployee);
        verify(personRepository, times(1)).findById(existingEmployee.getId());
    }

    @Test
    void testUpdateAnyPerson_NotFound() {
        // given
        Long personId = 1L;
        UpdatePersonCommand command = new UpdatePersonCommand();
        command.setType("EMPLOYEE");

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            personService.updateAnyPerson(personId, command);
        });
        // then
        assertEquals("Person not found with ID: 1", exception.getMessage());
        verify(personRepository, times(1)).findById(personId);
    }

    @Test
    public void searchPersons_ShouldReturnStudentsInHeightRangeInclusiveHeight() {

        Student student1 = new Student();
        student1.setType("STUDENT");
        student1.setName("John");
        student1.setSurname("Doe");
        student1.setPesel("1234567890");
        student1.setHeight(110.0);
        student1.setWeight(75.0);
        student1.setEmailAddress("john.doe@example.com");
        student1.setCourseName("Inzyniernia");
        student1.setScholarship(4000);
        student1.setNameOfUniversity("Politechnika Poznanska");

        personRepository.save(student1);

        Student student2 = new Student();
        student2.setType("STUDENT");
        student2.setName("Darek");
        student2.setSurname("Doe");
        student2.setPesel("1234566890");
        student2.setHeight(180.0);
        student2.setWeight(75.0);
        student2.setEmailAddress("john.doe@example.com");
        student2.setCourseName("Inzyniernia");
        student2.setScholarship(4000);
        student2.setNameOfUniversity("Politechnika Poznanska");

        personRepository.save(student2);

        List<SearchCriteria> searchCriteria = List.of(
                new SearchCriteria("height", "range", "180.0", "200.0")
        );

        Pageable pageable = PageRequest.of(0, 10);

        // Perform search
        Page<PersonDto> result = personService.searchPersons(searchCriteria, pageable);

        // Assertions
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Darek");
        assertThat(result.getContent().get(0).getHeight()).isEqualTo(180.0);
    }

    @Test
    void testUpdateAnyPerson_Failure_ShouldNotIncrementVersion() {
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
        existingEmployee.setVersion(0);

        UpdateEmployeeCommand command = new UpdateEmployeeCommand();
        command.setType("EMPLOYEE");
        command.setName("NOWE IMIE");
        command.setVersion("v2");

        personRepository.save(existingEmployee);
        // when
        assertThrows(ResourceNotFoundException.class, () -> {
            personService.updateAnyPerson(2L, command);
        });

        // then
        assertEquals(0, existingEmployee.getVersion());
        assertEquals(existingEmployee.getName(), existingEmployee.getName());

    }
}