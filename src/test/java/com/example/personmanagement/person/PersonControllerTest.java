package com.example.personmanagement.person;

import com.example.personmanagement.employee.EmployeeCreationStrategy;
import com.example.personmanagement.employee.model.CreateEmployeeCommand;
import com.example.personmanagement.employee.model.EmployeeDto;
import com.example.personmanagement.person.model.CreatePersonCommand;
import com.example.personmanagement.person.model.Person;
import com.example.personmanagement.person.model.PersonDto;
import com.example.personmanagement.student.model.CreateStudentCommand;
import com.example.personmanagement.student.model.Student;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.port;
import static javax.security.auth.callback.ConfirmationCallback.OK;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
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

    @Mock
    private PersonRepository personRepository;

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

        Person newPerson = creationStrategy.create(createEmployeeCommand);
        return newPerson;
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

        String exceptionMsg = "invalid Polish National Identification Number (PESEL)";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createEmployeeCommand)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("validation errors"))
                .andExpect(jsonPath("$.violations[0].field").value("pesel"))
                .andExpect(jsonPath("$.violations[0].message").value(exceptionMsg));
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
    void searchWithoutParameters() throws Exception {
        mockMvc.perform(get("/api/people")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void searchByEmployeeType() throws Exception {
        mockMvc.perform(get("/api/people")
                        .param("type", "EMPLOYEE")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void searchByEmployeeTypeWithHeightRange() throws Exception {
        mockMvc.perform(get("/api/people")
                        .param("type", "EMPLOYEE")
                        .param("heightFrom", "150")
                        .param("heightTo", "180")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}