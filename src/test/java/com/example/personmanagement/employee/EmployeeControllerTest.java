package com.example.personmanagement.employee;

import com.devskiller.jfairy.Fairy;
import com.example.personmanagement.model.employee.CreateEmployeeCommand;
import com.example.personmanagement.model.employee.EmployeeDto;
import com.example.personmanagement.model.position.CreatePositionCommand;
import com.example.personmanagement.repository.EmployeeRepository;
import com.example.personmanagement.repository.PersonRepository;
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
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.Locale;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmployeeControllerTest {

    private static final Fairy fairy = Fairy.create(new Locale("pl"));

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private EmployeeRepository employeeRepository;


    @Test
    @WithMockUser(roles = "ADMIN")
    void addJobPositionToPerson() throws Exception {
        // given
        var employee = postRandomEmployee();
        var personId = employee.getId();

        // when
        CreatePositionCommand command = new CreatePositionCommand("Developer", LocalDate.now(), LocalDate.now().plusDays(5), 2000);
        ResultActions resultActions = postJobPosition(personId, command);

        // then
        resultActions
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void givenEmployeeWithPosition_WhenAddOverlappingJobPosition_ThenShouldFail() throws Exception {
        // given
        var now = LocalDate.now();
        var employee = postRandomEmployee();
        var personId = employee.getId();
        var jobPosition1 = new CreatePositionCommand("Developer", now, now.plusDays(5), 2000);
        postJobPosition(personId, jobPosition1);

        // when
        var overlappingJobPosition = new CreatePositionCommand("Senior Developer", now, now.plusDays(5), 5000);
        ResultActions resultActions = postJobPosition(personId, overlappingJobPosition);

        // then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("New job position overlaps with existing position"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void givenEmployeeWithPosition_WhenUpdatingJobPosition_ThenShouldPass() throws Exception {
        // given
        var now = LocalDate.now();
        var employee = postRandomEmployee();
        var personId = employee.getId();
        CreatePositionCommand command = new CreatePositionCommand();
        command.setPositionName("Developer");
        command.setStartDate(LocalDate.now());
        command.setEndDate(LocalDate.now().plusDays(5));
        command.setSalary(2000);
        postJobPosition(personId, command);

        // when
        var updatingJobPosition = new CreatePositionCommand("Senior Developer", now.plusDays(10), now.plusDays(15), 5000);
        ResultActions resultActions = postJobPosition(personId, updatingJobPosition);

        // then
        resultActions
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void givenEmployeesWithSamePESEL_WhenCreateEmployees_ThenShouldFail() throws Exception {
        // given
        String pesel = "59052491861";

        CreateEmployeeCommand employee1 = new CreateEmployeeCommand();
        employee1.setType("EMPLOYEE");
        employee1.setName("John");
        employee1.setSurname("Doe");
        employee1.setEmailAddress("john@example.com");
        employee1.setPesel(pesel);
        postEmployee(employee1);

        // when
        CreateEmployeeCommand employee2 = new CreateEmployeeCommand();
        employee2.setType("EMPLOYEE");
        employee2.setName("Jane");
        employee2.setSurname("Doe");
        employee2.setEmailAddress("jane@example.com");
        employee2.setPesel(pesel);

        mockMvc.perform(post("/api/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee2)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.timestamp").exists());
    }


    private ResultActions postJobPosition(Long personId, CreatePositionCommand command) throws Exception {
        return mockMvc.perform(post("/api/employees/{personId}/positions", personId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command))
                .accept(MediaType.APPLICATION_JSON));
    }

    private EmployeeDto postRandomEmployee() throws Exception {
        var person = fairy.person();
        var requestBody = new CreateEmployeeCommand();
        requestBody.setType("EMPLOYEE");
        requestBody.setName(person.getFirstName());
        requestBody.setSurname(person.getLastName());
        requestBody.setEmailAddress(person.getEmail());
        requestBody.setPesel(person.getNationalIdentificationNumber());
        requestBody.setHeight(175.5);
        requestBody.setWeight(70.2);
        return postEmployee(requestBody);
    }

    private EmployeeDto postEmployee(CreateEmployeeCommand requestBody) throws Exception {
        var result = mockMvc.perform(post("/api/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return objectMapper.readValue(result.getResponse().getContentAsString(), EmployeeDto.class);
    }

    @AfterEach
    public void setUp() {
        personRepository.deleteAll();
        employeeRepository.deleteAll();
    }


}