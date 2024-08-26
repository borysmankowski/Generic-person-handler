package com.example.personmanagement.controller;

import com.example.personmanagement.exception.ResourceNotFoundException;
import com.example.personmanagement.model.employee.Employee;
import com.example.personmanagement.model.employee.EmployeeDto;
import com.example.personmanagement.model.person.CreatePersonCommand;
import com.example.personmanagement.model.person.Person;
import com.example.personmanagement.model.person.UpdatePersonCommand;
import com.example.personmanagement.repository.PersonRepository;
import com.example.personmanagement.search.SearchCriteria;
import com.example.personmanagement.strategy.EmployeeCreationStrategy;
import com.example.personmanagement.strategy.PersonCreationStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mickaelb.api.AssertHibernateSQLCount;
import com.mickaelb.integration.spring.HibernateAssertTestListener;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestExecutionListeners(listeners = HibernateAssertTestListener.class, mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PersonRepository personRepository;

    private static Person getPerson() {

        CreatePersonCommand createPersonCommand = new CreatePersonCommand();
        createPersonCommand.setType("EMPLOYEE");
        createPersonCommand.setName("John");
        createPersonCommand.setSurname("Doe");
        createPersonCommand.setEmailAddress("john@example.com");
        createPersonCommand.setPesel("50071262432");
        createPersonCommand.setHeight(175.0);
        createPersonCommand.setWeight(70.0);

        PersonCreationStrategy creationStrategy = new EmployeeCreationStrategy();
        return creationStrategy.create(createPersonCommand);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createPerson() throws Exception {

        Person newPerson = getPerson();

        String jsonRequest = objectMapper.writeValueAsString(newPerson);

        mockMvc.perform(post("/api/people").contentType(MediaType.APPLICATION_JSON).content(jsonRequest)).andExpect(status().isCreated()).andExpect(jsonPath("$.name").value(newPerson.getName())).andExpect(jsonPath("$.surname").value(newPerson.getSurname())).andExpect(jsonPath("$.emailAddress").value(newPerson.getEmailAddress()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void CreatePersonFailureBlankName() throws Exception {

        CreatePersonCommand createPersonCommand = new CreatePersonCommand();
        createPersonCommand.setType("EMPLOYEE");
        createPersonCommand.setName(" ");
        createPersonCommand.setSurname("Doe");
        createPersonCommand.setEmailAddress("john@example.com");
        createPersonCommand.setPesel("50071262432");
        createPersonCommand.setHeight(175.0);
        createPersonCommand.setWeight(70.0);

        String exceptionMsg = "Name cannot be blank";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPersonCommand)))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message")
                        .value("validation errors"))
                .andExpect(jsonPath("$.violations[0].field")
                        .value("name")).andExpect(jsonPath("$.violations[0].message").value(exceptionMsg));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void CreatePersonFailureBlankSurname() throws Exception {

        CreatePersonCommand createPersonCommand = new CreatePersonCommand();
        createPersonCommand.setType("EMPLOYEE");
        createPersonCommand.setName("John");
        createPersonCommand.setSurname(" ");
        createPersonCommand.setEmailAddress("john@example.com");
        createPersonCommand.setPesel("50071262432");
        createPersonCommand.setHeight(175.0);
        createPersonCommand.setWeight(70.0);

        String exceptionMsg = "Surname cannot be blank";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPersonCommand)))
                .andDo(print())
                .andExpect(status()
                        .isBadRequest())
                .andExpect(jsonPath("$.timestamp")
                        .exists())
                .andExpect(jsonPath("$.message")
                        .value("validation errors"))
                .andExpect(jsonPath("$.violations[0].field")
                        .value("surname"))
                .andExpect(jsonPath("$.violations[0].message")
                        .value(exceptionMsg));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void CreatePersonFailureBlankPesel() throws Exception {

        CreatePersonCommand createPersonCommand = new CreatePersonCommand();
        createPersonCommand.setType("EMPLOYEE");
        createPersonCommand.setName("John");
        createPersonCommand.setSurname("Doe");
        createPersonCommand.setEmailAddress("john@example.com");
        createPersonCommand.setPesel(" ");
        createPersonCommand.setHeight(175.0);
        createPersonCommand.setWeight(70.0);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPersonCommand)))
                .andDo(print()).andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message")
                        .value("validation errors"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void CreatePersonFailureBlankEmail() throws Exception {

        CreatePersonCommand createPersonCommand = new CreatePersonCommand();
        createPersonCommand.setType("EMPLOYEE");
        createPersonCommand.setName("John");
        createPersonCommand.setSurname("Doe");
        createPersonCommand.setEmailAddress(" ");
        createPersonCommand.setPesel("50071262432");
        createPersonCommand.setHeight(175.0);
        createPersonCommand.setWeight(70.0);

        String exceptionMsg = "must be a well-formed email address";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPersonCommand)))
                .andDo(print()).andExpect(status()
                        .isBadRequest()).andExpect(jsonPath("$.timestamp")
                        .exists()).andExpect(jsonPath("$.message")
                        .value("validation errors")).andExpect(jsonPath("$.violations[0].field")
                        .value("emailAddress"))
                .andExpect(jsonPath("$.violations[0].message")
                        .value(exceptionMsg));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void givenStudentsWithSamePESEL_WhenCreateStudents_ThenShouldFail() throws Exception {
        String pesel = "99010264551";

        CreatePersonCommand student1 = new CreatePersonCommand();
        student1.setType("STUDENT");

        student1.setName("John");
        student1.setSurname("Doe");
        student1.setEmailAddress("john@example.com");
        student1.setPesel(pesel);
        student1.setHeight(175.0);
        student1.setWeight(70.0);

        HashMap<String, String> params2 = new HashMap<>();
        params2.put("nameOfUniversity", "UniName");
        params2.put("yearOfStudies", "2020");
        params2.put("courseName", "CourseName");
        params2.put("scholarship", "2000");

        student1.setPersonUniqueFields(params2);

        postStudent(student1);

        CreatePersonCommand createPersonCommandClone = new CreatePersonCommand();

        createPersonCommandClone.setType("STUDENT");
        createPersonCommandClone.setName("John");
        createPersonCommandClone.setSurname("Doe");
        createPersonCommandClone.setEmailAddress("john@example.com");
        createPersonCommandClone.setPesel(pesel);
        createPersonCommandClone.setHeight(175.0);
        createPersonCommandClone.setWeight(70.0);

        HashMap<String, String> params1 = new HashMap<>();
        params1.put("nameOfUniversity", "UniName");
        params1.put("yearOfStudies", "2020");
        params1.put("courseName", "CourseName");
        params1.put("scholarship", "2000");
        createPersonCommandClone.setPersonUniqueFields(params1);


        mockMvc.perform(post("/api/people").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPersonCommandClone)))
                .andExpect(status()
                        .isBadRequest())
                .andExpect(jsonPath("$.timestamp")
                        .exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void givenPensionersWithSamePESEL_WhenCreatePensioners_ThenShouldFail() throws Exception {

        String pesel = "99010264551";

        CreatePersonCommand pensioner1 = new CreatePersonCommand();
        pensioner1.setType("PENSIONER");
        pensioner1.setName("John");
        pensioner1.setSurname("Doe");
        pensioner1.setEmailAddress("john@example.com");
        pensioner1.setPesel(pesel);
        pensioner1.setHeight(175.0);
        pensioner1.setWeight(70.0);

        HashMap<String, String> params1 = new HashMap<>();
        params1.put("pensionAmount", "2000.0");
        params1.put("workedYears", "30");
        pensioner1.setPersonUniqueFields(params1);

        postPensioner(pensioner1);

        CreatePersonCommand pensioner2 = new CreatePersonCommand();
        pensioner2.setType("PENSIONER");
        pensioner2.setName("Jane");
        pensioner2.setSurname("Doe");
        pensioner2.setEmailAddress("jane@example.com");
        pensioner2.setPesel(pesel);
        pensioner2.setHeight(160.0);
        pensioner2.setWeight(60.0);

        HashMap<String, String> params2 = new HashMap<>();
        params2.put("pensionAmount", "1800.0");
        params2.put("workedYears", "25");
        pensioner2.setPersonUniqueFields(params2);


        mockMvc.perform(post("/api/people").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pensioner2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp")
                        .exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void UpdatePersonDetails_ShouldIncrementVersion() throws Exception {
        UpdatePersonCommand updateEmployeeCommand = new UpdatePersonCommand();
        updateEmployeeCommand.setType("EMPLOYEE");
        updateEmployeeCommand.setName("newName");
        updateEmployeeCommand.setSurname("newSurname");
        updateEmployeeCommand.setPesel("00250714618");
        updateEmployeeCommand.setHeight(180);
        updateEmployeeCommand.setWeight(80);
        updateEmployeeCommand.setEmailAddress("newemail@test.com");
        updateEmployeeCommand.setVersion(0);

        CreatePersonCommand createEmployeeCommand = new CreatePersonCommand();
        createEmployeeCommand.setType("EMPLOYEE");
        createEmployeeCommand.setName("John");
        createEmployeeCommand.setSurname("Doe");
        createEmployeeCommand.setEmailAddress("john@example.com");
        createEmployeeCommand.setPesel("50071262432");
        createEmployeeCommand.setHeight(175.0);
        createEmployeeCommand.setWeight(70.0);

        EmployeeDto employee = postEmployee(createEmployeeCommand);

        Employee employeeBeforeUpdate = (Employee) personRepository.findById(employee.getId()).orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employee.getId()));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/people/{personId}", employee.getId()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateEmployeeCommand))).andDo(print()).andExpect(status().isOk());

        Employee employeeAfterUpdate = (Employee) personRepository.findById(employee.getId()).orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employee.getId()));

        assertNotEquals(employeeBeforeUpdate.getVersion(), employeeAfterUpdate.getVersion());
        assertEquals(1, employeeAfterUpdate.getVersion());

        assertEquals(updateEmployeeCommand.getName(), employeeAfterUpdate.getName());
        assertEquals(updateEmployeeCommand.getSurname(), employeeAfterUpdate.getSurname());
        assertEquals(updateEmployeeCommand.getEmailAddress(), employeeAfterUpdate.getEmailAddress());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Transactional
    @AssertHibernateSQLCount(inserts = 1, selects = 1, updates = 1, deletes = 1)
    void updatePersonDetails_ShouldIncrementVersion_ShouldPerform2Queries() throws Exception {
        UpdatePersonCommand updateEmployeeCommand = new UpdatePersonCommand();
        updateEmployeeCommand.setType("EMPLOYEE");
        updateEmployeeCommand.setName("newName");
        updateEmployeeCommand.setSurname("newSurname");
        updateEmployeeCommand.setPesel("00250714618");
        updateEmployeeCommand.setHeight(180);
        updateEmployeeCommand.setWeight(80);
        updateEmployeeCommand.setEmailAddress("newemail@test.com");
        updateEmployeeCommand.setVersion(0);

        CreatePersonCommand createEmployeeCommand = new CreatePersonCommand();
        createEmployeeCommand.setType("EMPLOYEE");
        createEmployeeCommand.setName("name");
        createEmployeeCommand.setSurname("Surname");
        createEmployeeCommand.setPesel("00250714618");
        createEmployeeCommand.setHeight(100);
        createEmployeeCommand.setWeight(100);
        createEmployeeCommand.setEmailAddress("email@email.com");

        EmployeeDto employee = postEmployee(createEmployeeCommand);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/people/{personId}", employee.getId()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateEmployeeCommand))).andDo(print()).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void UpdatePersonDetailsShouldFailDueToLostUpdate_HigherVersion_VersionNotIncremented() throws Exception {
        UpdatePersonCommand updateEmployeeCommandVersionDifference = new UpdatePersonCommand();
        updateEmployeeCommandVersionDifference.setType("EMPLOYEE");
        updateEmployeeCommandVersionDifference.setName("newName");
        updateEmployeeCommandVersionDifference.setSurname("newSurname");
        updateEmployeeCommandVersionDifference.setPesel("00250714618");
        updateEmployeeCommandVersionDifference.setHeight(180);
        updateEmployeeCommandVersionDifference.setWeight(80);
        updateEmployeeCommandVersionDifference.setEmailAddress("newemail@test.com");
        updateEmployeeCommandVersionDifference.setVersion(100);

        CreatePersonCommand createEmployeeCommand = new CreatePersonCommand();
        createEmployeeCommand.setType("EMPLOYEE");
        createEmployeeCommand.setName("name");
        createEmployeeCommand.setSurname("Surname");
        createEmployeeCommand.setPesel("00250714618");
        createEmployeeCommand.setHeight(100);
        createEmployeeCommand.setWeight(100);
        createEmployeeCommand.setEmailAddress("email@email.com");

        EmployeeDto employee = postEmployee(createEmployeeCommand);

        Employee employeeBeforeUpdate = (Employee) personRepository.findById(employee.getId()).orElseThrow(()
                -> new ResourceNotFoundException("Employee not found with ID: " + employee.getId()));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/people/{personId}", employee.getId())
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateEmployeeCommandVersionDifference)))
                .andDo(print()).andExpect(status().isConflict());

        Employee employeeAfterUpdate = (Employee) personRepository.findById(employee.getId()).orElseThrow(()
                -> new ResourceNotFoundException("Employee not found with ID: " + employee.getId()));

        assertEquals(employeeBeforeUpdate.getVersion(), employeeAfterUpdate.getVersion());

        assertNotEquals(updateEmployeeCommandVersionDifference.getName(), employeeAfterUpdate.getName());
        assertNotEquals(updateEmployeeCommandVersionDifference.getSurname(), employeeAfterUpdate.getSurname());
        assertNotEquals(updateEmployeeCommandVersionDifference.getEmailAddress(), employeeAfterUpdate.getEmailAddress());
    }

    @Test
    void searchWithoutParameters() throws Exception {

        List<String> list = new ArrayList<>();

        String searchCriteriaJson = objectMapper.writeValueAsString(list);

        mockMvc.perform(get("/api/people").param("search-criteria", searchCriteriaJson).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    void searchByEmployeeType() throws Exception {

        SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setKey("type");
        searchCriteria.setOperation("eq");
        searchCriteria.setValue("EMPLOYEE");
        List<SearchCriteria> searchCriteriaList = List.of(searchCriteria);
        String searchCriteriaJson = objectMapper.writeValueAsString(searchCriteriaList);

        mockMvc.perform(get("/api/people").param("search-criteria", searchCriteriaJson).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    void searchByEmployeeTypeWithHeightRange() throws Exception {
        SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setKey("height");
        searchCriteria.setOperation("range");
        searchCriteria.setValue("100");
        searchCriteria.setSecondValue("200");
        List<SearchCriteria> searchCriteriaList = List.of(searchCriteria);

        String searchCriteriaJson = objectMapper.writeValueAsString(searchCriteriaList);

        mockMvc.perform(get("/api/people").param("search-criteria", searchCriteriaJson).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    void searchByEmployeeTypeWithEqualValueToSecondValueHeightRange() throws Exception {
        SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setKey("height");
        searchCriteria.setOperation("range");
        searchCriteria.setValue("200");
        searchCriteria.setSecondValue("200");
        List<SearchCriteria> searchCriteriaList = List.of(searchCriteria);

        String searchCriteriaJson = objectMapper.writeValueAsString(searchCriteriaList);

        mockMvc.perform(get("/api/people").param("search-criteria", searchCriteriaJson).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    void searchByEmployeeTypeWithWrongHeightRange() throws Exception {
        SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setKey("height");
        searchCriteria.setOperation("range");
        searchCriteria.setValue("200");
        searchCriteria.setSecondValue("100");
        List<SearchCriteria> searchCriteriaList = List.of(searchCriteria);

        String searchCriteriaJson = objectMapper.writeValueAsString(searchCriteriaList);

        mockMvc.perform(get("/api/people").param("search-criteria", searchCriteriaJson).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    void searchByEmployeeTypeWithSalaryRange() throws Exception {
        SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setKey("salary");
        searchCriteria.setOperation("salaryRange");
        searchCriteria.setValue("1000");
        searchCriteria.setSecondValue("2000");
        List<SearchCriteria> searchCriteriaList = List.of(searchCriteria);

        String searchCriteriaJson = objectMapper.writeValueAsString(searchCriteriaList);

        mockMvc.perform(get("/api/people").param("search-criteria", searchCriteriaJson).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    void searchByEmployeeTypeWithValueEqualToSecondValueSalaryRange() throws Exception {
        SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setKey("salary");
        searchCriteria.setOperation("salaryRange");
        searchCriteria.setValue("2000");
        searchCriteria.setSecondValue("2000");
        List<SearchCriteria> searchCriteriaList = List.of(searchCriteria);

        String searchCriteriaJson = objectMapper.writeValueAsString(searchCriteriaList);

        mockMvc.perform(get("/api/people").param("search-criteria", searchCriteriaJson).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    void searchByEmployeeTypeWithWrongSalaryRange() throws Exception {
        SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setKey("salary");
        searchCriteria.setOperation("salaryRange");
        searchCriteria.setValue("2000");
        searchCriteria.setSecondValue("1000");
        List<SearchCriteria> searchCriteriaList = List.of(searchCriteria);

        String searchCriteriaJson = objectMapper.writeValueAsString(searchCriteriaList);

        mockMvc.perform(get("/api/people").param("search-criteria", searchCriteriaJson).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    void postPensioner(CreatePersonCommand pensioner) throws Exception {
        mockMvc.perform(post("/api/people").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(pensioner))).andExpect(status().isCreated());
    }

    void postStudent(CreatePersonCommand student) throws Exception {
        mockMvc.perform(post("/api/people").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(student))).andExpect(status().isCreated());
    }

    private EmployeeDto postEmployee(CreatePersonCommand requestBody) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/people").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(requestBody)).accept(MediaType.APPLICATION_JSON)).andReturn();
        return objectMapper.readValue(result.getResponse().getContentAsString(), EmployeeDto.class);
    }

    @AfterEach
    void tearDown() {
        personRepository.deleteAll();
    }
}