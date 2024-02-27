package com.example.personmanagement.employee;

import com.devskiller.jfairy.Fairy;
import com.example.personmanagement.employee.model.AddJobPositionCommand;
import com.example.personmanagement.employee.model.CreateEmployeeCommand;
import com.example.personmanagement.employee.model.Employee;
import com.example.personmanagement.employee.model.EmployeeDto;
import com.example.personmanagement.employee.model.JobPosition;
import com.example.personmanagement.person.PersonRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
    


    @Test
    @WithMockUser(roles = "ADMIN")
    void addJobPositionToPerson() throws Exception {
        // given
        var employee = postRandomEmployee();
        var personId = employee.getId();

        // when
        AddJobPositionCommand command = new AddJobPositionCommand("Developer", LocalDate.now(), LocalDate.now().plusDays(5), 2000);
        ResultActions resultActions = postJobPosition(personId, command);

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Job position added successfully"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void givenEmployeeWithPosition_WhenAddOverlappingJobPosition_ThenShouldFail() throws Exception {
        // given
        var now = LocalDate.now();
        var employee = postRandomEmployee();
        var personId = employee.getId();
        var jobPosition1 = new AddJobPositionCommand("Developer", now, now.plusDays(5), 2000);
        postJobPosition(personId, jobPosition1);

        // when
        var overlappingJobPosition = new AddJobPositionCommand("Senior Developer", now, now.plusDays(5), 5000);
        ResultActions resultActions = postJobPosition(personId, overlappingJobPosition);

        // then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("New job position overlaps with existing position"));
    }


    private ResultActions postJobPosition(Long personId, AddJobPositionCommand command) throws Exception {
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


}