package com.example.personmanagement.person;

import com.example.personmanagement.strategy.EmployeeCreationStrategy;
import com.example.personmanagement.model.employee.CreateEmployeeCommand;
import com.example.personmanagement.model.employee.EmployeeDto;
import com.example.personmanagement.model.employee.UpdateEmployeeCommand;
import com.example.personmanagement.model.pensioner.CreatePensionerCommand;
import com.example.personmanagement.model.person.Person;
import com.example.personmanagement.search.SearchCriteria;
import com.example.personmanagement.model.student.CreateStudentCommand;
import com.example.personmanagement.repository.PersonRepository;
import com.example.personmanagement.strategy.PersonCreationStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PersonRepository personRepository;

    private static Person getPerson() {
        CreateEmployeeCommand createEmployeeCommand = new CreateEmployeeCommand();
        createEmployeeCommand.setType("EMPLOYEE");
        createEmployeeCommand.setName("name");
        createEmployeeCommand.setSurname("surname");
        createEmployeeCommand.setPesel("50071262432");
        createEmployeeCommand.setHeight(100);
        createEmployeeCommand.setWeight(100);
        createEmployeeCommand.setEmailAddress("emailAddress@test.com");

        PersonCreationStrategy creationStrategy = new EmployeeCreationStrategy();

        return creationStrategy.create(createEmployeeCommand);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createPerson() throws Exception {

        Person newPerson = getPerson();

        String jsonRequest = objectMapper.writeValueAsString(newPerson);

        mockMvc.perform(
                        post("/api/people")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(newPerson.getName()))
                .andExpect(jsonPath("$.surname").value(newPerson.getSurname()))
                .andExpect(jsonPath("$.emailAddress").value(newPerson.getEmailAddress()));

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void CreatePersonFailureBlankName() throws Exception {

        CreateEmployeeCommand createEmployeeCommand = new CreateEmployeeCommand();
        createEmployeeCommand.setType("EMPLOYEE");
        createEmployeeCommand.setName(" ");
        createEmployeeCommand.setSurname("surname");
        createEmployeeCommand.setPesel("77080165949");
        createEmployeeCommand.setHeight(100);
        createEmployeeCommand.setWeight(100);
        createEmployeeCommand.setEmailAddress("test@test.com");

        String exceptionMsg = "Name cannot be blank";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createEmployeeCommand)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("validation errors"))
                .andExpect(jsonPath("$.violations[0].field").value("name"))
                .andExpect(jsonPath("$.violations[0].message").value(exceptionMsg));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void CreatePersonFailureBlankSurname() throws Exception {

        CreateEmployeeCommand createEmployeeCommand = new CreateEmployeeCommand();
        createEmployeeCommand.setType("EMPLOYEE");
        createEmployeeCommand.setName("name");
        createEmployeeCommand.setSurname(" ");
        createEmployeeCommand.setPesel("77080165949");
        createEmployeeCommand.setHeight(100);
        createEmployeeCommand.setWeight(100);
        createEmployeeCommand.setEmailAddress("test@test.com");

        String exceptionMsg = "Surname cannot be blank";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createEmployeeCommand)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("validation errors"))
                .andExpect(jsonPath("$.violations[0].field").value("surname"))
                .andExpect(jsonPath("$.violations[0].message").value(exceptionMsg));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void CreatePersonFailureBlankPesel() throws Exception {

        CreateEmployeeCommand createEmployeeCommand = new CreateEmployeeCommand();
        createEmployeeCommand.setType("EMPLOYEE");
        createEmployeeCommand.setName("name");
        createEmployeeCommand.setSurname("Surname");
        createEmployeeCommand.setPesel(" ");
        createEmployeeCommand.setHeight(100);
        createEmployeeCommand.setWeight(100);
        createEmployeeCommand.setEmailAddress("email@email.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createEmployeeCommand)))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("validation errors"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void UpdatePersonDetails() throws Exception {
        UpdateEmployeeCommand updateEmployeeCommand = new UpdateEmployeeCommand();
        updateEmployeeCommand.setType("EMPLOYEE");
        updateEmployeeCommand.setName("newName");
        updateEmployeeCommand.setSurname("newSurname");
        updateEmployeeCommand.setPesel("00250714618");
        updateEmployeeCommand.setHeight(180);
        updateEmployeeCommand.setWeight(80);
        updateEmployeeCommand.setEmailAddress("newemail@test.com");
        updateEmployeeCommand.setVersion("v1");

        CreateEmployeeCommand createEmployeeCommand = new CreateEmployeeCommand();
        createEmployeeCommand.setType("EMPLOYEE");
        createEmployeeCommand.setName("name");
        createEmployeeCommand.setSurname("Surname");
        createEmployeeCommand.setPesel("00250714618");
        createEmployeeCommand.setHeight(100);
        createEmployeeCommand.setWeight(100);
        createEmployeeCommand.setEmailAddress("email@email.com");

        EmployeeDto employee = postEmployee(createEmployeeCommand);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/people/{personId}", employee.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateEmployeeCommand)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updateEmployeeCommand.getName()))
                .andExpect(jsonPath("$.surname").value(updateEmployeeCommand.getSurname()))
                .andExpect(jsonPath("$.emailAddress").value(updateEmployeeCommand.getEmailAddress()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void CreatePersonFailureBlankEmail() throws Exception {

        CreateEmployeeCommand createEmployeeCommand = new CreateEmployeeCommand();
        createEmployeeCommand.setType("EMPLOYEE");
        createEmployeeCommand.setName("name");
        createEmployeeCommand.setSurname("Surname");
        createEmployeeCommand.setPesel("77080165949");
        createEmployeeCommand.setHeight(100);
        createEmployeeCommand.setWeight(100);
        createEmployeeCommand.setEmailAddress(" ");

        String exceptionMsg = "must be a well-formed email address";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createEmployeeCommand)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("validation errors"))
                .andExpect(jsonPath("$.violations[0].field").value("emailAddress"))
                .andExpect(jsonPath("$.violations[0].message").value(exceptionMsg));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void givenStudentsWithSamePESEL_WhenCreateStudents_ThenShouldFail() throws Exception {
        // given
        String pesel = "99010264551";

        CreateStudentCommand student1 = new CreateStudentCommand();
        student1.setType("STUDENT");
        student1.setName("John");
        student1.setSurname("Doe");
        student1.setEmailAddress("john@example.com");
        student1.setPesel(pesel);
        student1.setNameOfUniversity("University A");
        student1.setYearOfStudies(2);
        student1.setCourseName("Computer Science");
        student1.setScholarship(1000.0);
        postStudent(student1);

        // when
        CreateStudentCommand student2 = new CreateStudentCommand();
        student2.setType("STUDENT");
        student2.setName("Jane");
        student2.setSurname("Doe");
        student2.setEmailAddress("jane@example.com");
        student2.setPesel(pesel);
        student2.setNameOfUniversity("University B");
        student2.setYearOfStudies(3);
        student2.setCourseName("Electrical Engineering");
        student2.setScholarship(1200.0);

        mockMvc.perform(post("/api/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student2)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void givenPensionersWithSamePESEL_WhenCreatePensioners_ThenShouldFail() throws Exception {
        // given
        String pesel = "99010264551";

        CreatePensionerCommand pensioner1 = new CreatePensionerCommand();
        pensioner1.setType("PENSIONER");
        pensioner1.setName("John");
        pensioner1.setSurname("Doe");
        pensioner1.setEmailAddress("john@example.com");
        pensioner1.setPesel(pesel);
        pensioner1.setPensionAmount(2000.0);
        pensioner1.setWorkedYears(30);
        postPensioner(pensioner1);

        // when
        CreatePensionerCommand pensioner2 = new CreatePensionerCommand();
        pensioner2.setType("PENSIONER");
        pensioner2.setName("Jane");
        pensioner2.setSurname("Doe");
        pensioner2.setEmailAddress("jane@example.com");
        pensioner2.setPesel(pesel);
        pensioner2.setPensionAmount(1800.0);
        pensioner2.setWorkedYears(25);

        mockMvc.perform(post("/api/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pensioner2)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    void postPensioner(CreatePensionerCommand pensioner) throws Exception {
        mockMvc.perform(post("/api/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pensioner)))
                .andExpect(status().isCreated());
    }


    void postStudent(CreateStudentCommand student) throws Exception {
        mockMvc.perform(post("/api/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isCreated());
    }

    private EmployeeDto postEmployee(CreateEmployeeCommand requestBody) throws Exception {
        var result = mockMvc.perform(post("/api/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return objectMapper.readValue(result.getResponse().getContentAsString(), EmployeeDto.class);
    }

    @Test
    void searchWithoutParameters() throws Exception {

        List<String> list = new ArrayList<>();

        String searchCriteriaJson = objectMapper.writeValueAsString(list);

        mockMvc.perform(get("/api/people")
                        .param("search-criteria", searchCriteriaJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    void searchByEmployeeType() throws Exception {

        SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setKey("type");
        searchCriteria.setOperation("eq");
        searchCriteria.setValue("EMPLOYEE");
        List<SearchCriteria> searchCriteriaList = List.of(searchCriteria);
        String searchCriteriaJson = objectMapper.writeValueAsString(searchCriteriaList);

        mockMvc.perform(get("/api/people")
                        .param("search-criteria", searchCriteriaJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    void searchByEmployeeTypeWithHeightRange() throws Exception {
        SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setKey("height");
        searchCriteria.setOperation("range");
        searchCriteria.setValue("100");
        searchCriteria.setSecondValue("200");
        List<SearchCriteria> searchCriteriaList = List.of(searchCriteria
        );

        String searchCriteriaJson = objectMapper.writeValueAsString(searchCriteriaList);

        mockMvc.perform(get("/api/people")
                        .param("search-criteria", searchCriteriaJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void searchByEmployeeTypeWithEqualValueToSecondValueHeightRange() throws Exception {
        SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setKey("height");
        searchCriteria.setOperation("range");
        searchCriteria.setValue("200");
        searchCriteria.setSecondValue("200");
        List<SearchCriteria> searchCriteriaList = List.of(searchCriteria
        );

        String searchCriteriaJson = objectMapper.writeValueAsString(searchCriteriaList);

        mockMvc.perform(get("/api/people")
                        .param("search-criteria", searchCriteriaJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void searchByEmployeeTypeWithWrongHeightRange() throws Exception {
        SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setKey("height");
        searchCriteria.setOperation("range");
        searchCriteria.setValue("200");
        searchCriteria.setSecondValue("100");
        List<SearchCriteria> searchCriteriaList = List.of(searchCriteria
        );

        String searchCriteriaJson = objectMapper.writeValueAsString(searchCriteriaList);

        mockMvc.perform(get("/api/people")
                        .param("search-criteria", searchCriteriaJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchByEmployeeTypeWithSalaryRange() throws Exception {
        SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setKey("salary");
        searchCriteria.setOperation("salaryRange");
        searchCriteria.setValue("1000");
        searchCriteria.setSecondValue("2000");
        List<SearchCriteria> searchCriteriaList = List.of(searchCriteria
        );

        String searchCriteriaJson = objectMapper.writeValueAsString(searchCriteriaList);

        mockMvc.perform(get("/api/people")
                        .param("search-criteria", searchCriteriaJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void searchByEmployeeTypeWithValueEqualToSecondValueSalaryRange() throws Exception {
        SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setKey("salary");
        searchCriteria.setOperation("salaryRange");
        searchCriteria.setValue("2000");
        searchCriteria.setSecondValue("2000");
        List<SearchCriteria> searchCriteriaList = List.of(searchCriteria
        );

        String searchCriteriaJson = objectMapper.writeValueAsString(searchCriteriaList);

        mockMvc.perform(get("/api/people")
                        .param("search-criteria", searchCriteriaJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void searchByEmployeeTypeWithWrongSalaryRange() throws Exception {
        SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setKey("salary");
        searchCriteria.setOperation("salaryRange");
        searchCriteria.setValue("2000");
        searchCriteria.setSecondValue("1000");
        List<SearchCriteria> searchCriteriaList = List.of(searchCriteria
        );

        String searchCriteriaJson = objectMapper.writeValueAsString(searchCriteriaList);

        mockMvc.perform(get("/api/people")
                        .param("search-criteria", searchCriteriaJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @AfterEach
    public void setUp() {
        personRepository.deleteAll();
    }
}