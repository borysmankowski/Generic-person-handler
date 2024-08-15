package com.example.personmanagement.controller;

import com.example.personmanagement.model.file.FileInformation;
import com.example.personmanagement.model.file.FileStatus;
import com.example.personmanagement.model.file.FileUploadResponse;
import com.example.personmanagement.model.person.Person;
import com.example.personmanagement.repository.FileInformationRepository;
import com.example.personmanagement.repository.PersonRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.annotations.BatchSize;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FileInformationRepository fileInformationRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testFileLoaderEndpoint() throws Exception {
        // given
        Path filePath = Paths.get("files-to-import/generatedFileForTesting.csv");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "testFile.csv",
                MediaType.TEXT_PLAIN_VALUE,
                Files.readAllBytes(filePath)
        );

        var result = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/file-imports")
                        .file(file))
                .andExpect(status().isAccepted())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        FileUploadResponse uploadResponse = objectMapper.readValue(responseContent, FileUploadResponse.class);

        assertThat(uploadResponse.getId()).isNotNull();

        await().atMost(10, SECONDS).untilAsserted(() ->
                assertThat(personRepository.findAll()).isNotEmpty());    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @BatchSize(size = 3)
    void testFileLoaderEndpoint_ShouldHaveProcessedRecords() throws Exception {

        String uniqueFileName = "testFile-" + UUID.randomUUID() + ".csv";
        Path filePath = Paths.get("files-to-import/generatedFileForTesting.csv");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                uniqueFileName,
                MediaType.TEXT_PLAIN_VALUE,
                Files.readAllBytes(filePath)
        );

        var result = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/file-imports")
                        .file(file))
                .andExpect(status().isAccepted())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        FileUploadResponse uploadResponse = objectMapper.readValue(responseContent, FileUploadResponse.class);

        await().atMost(10, SECONDS).untilAsserted(() -> {
            List<FileInformation> fileInformationList = fileInformationRepository.findAll();
            assertThat(fileInformationList).anyMatch(fileInfo -> fileInfo.getFilePath().contains(uniqueFileName));
        });

        await().atMost(10, SECONDS).untilAsserted(() -> {
            List<FileInformation> fileStatus = fileInformationRepository.findAll();
            assertThat(fileStatus.stream().filter(fileInfo -> fileInfo.getFilePath().contains(uniqueFileName))
                    .findFirst().orElseThrow().getStatus()).isEqualTo(FileStatus.SUCCESS);
        });

        await().atMost(10, SECONDS).untilAsserted(() -> {
            List<Person> persons = personRepository.findAll();
            assertThat(persons).hasSize(3);

            assertThat(persons).anySatisfy(person -> {
                assertThat(person.getName()).isEqualTo("Malina");
                assertThat(person.getSurname()).isEqualTo("Eno");
                assertThat(person.getPesel()).isEqualTo("70081539775");
            });

            assertThat(persons).anySatisfy(person -> {
                assertThat(person.getName()).isEqualTo("Merle");
                assertThat(person.getSurname()).isEqualTo("Ilka");
                assertThat(person.getPesel()).isEqualTo("90122199526");
            });

            assertThat(persons).anySatisfy(person -> {
                assertThat(person.getName()).isEqualTo("Blondelle");
                assertThat(person.getSurname()).isEqualTo("Eachern");
                assertThat(person.getPesel()).isEqualTo("51010932991");
            });
        });

        await().atMost(10, SECONDS).untilAsserted(() -> {
            FileInformation fileInformation = fileInformationRepository.findById(uploadResponse.getId()).orElseThrow();
            assertThat(fileInformation.getFilePath()).contains(uniqueFileName);
        });
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @BatchSize(size = 1)
    void testFileLoaderEndpoint_ShouldRollbackDueToDuplicates() throws Exception {

        String uniqueFileName = "testFile-" + UUID.randomUUID() + ".csv";
        Path filePath = Paths.get("files-to-import/generatedFileForTestingDuplicatedPesel.csv");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                uniqueFileName,
                MediaType.TEXT_PLAIN_VALUE,
                Files.readAllBytes(filePath)
        );

        var result = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/file-imports")
                        .file(file))
                .andExpect(status().isAccepted())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        FileUploadResponse uploadResponse = objectMapper.readValue(responseContent, FileUploadResponse.class);

        await().atMost(10, SECONDS).untilAsserted(() -> {
            List<FileInformation> fileInformationList = fileInformationRepository.findAll();
            assertThat(fileInformationList).anyMatch(fileInfo -> fileInfo.getFilePath().contains(uniqueFileName));
        });

        await().atMost(10, SECONDS).untilAsserted(() -> {
            List<Person> persons = personRepository.findAll();
            assertThat(persons).isEmpty();
        });

        await().atMost(10, SECONDS).untilAsserted(() -> {
            List<FileInformation> fileStatus = fileInformationRepository.findAll();
            assertThat(fileStatus.stream().filter(fileInfo -> fileInfo.getFilePath().contains(uniqueFileName))
                    .findFirst().orElseThrow().getStatus()).isEqualTo(FileStatus.FAILED);
        });

        await().atMost(10, SECONDS).untilAsserted(() -> {
            FileInformation fileInformation = fileInformationRepository.findById(uploadResponse.getId()).orElseThrow();
            assertThat(fileInformation.getFilePath()).contains(uniqueFileName);
        });
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testFileLoaderEndpoint_QueueImports() throws Exception {

        String uniqueFileName1 = "testFile1-" + UUID.randomUUID() + ".csv";
        String uniqueFileName2 = "testFile2-" + UUID.randomUUID() + ".csv";

        Path filePath1 = Paths.get("files-to-import/generatedFileForTesting.csv");
        Path filePath2 = Paths.get("files-to-import/generatedFileForTestingQueue.csv");

        MockMultipartFile file1 = new MockMultipartFile(
                "file",
                uniqueFileName1,
                MediaType.TEXT_PLAIN_VALUE,
                Files.readAllBytes(filePath1)
        );

        MockMultipartFile file2 = new MockMultipartFile(
                "file",
                uniqueFileName2,
                MediaType.TEXT_PLAIN_VALUE,
                Files.readAllBytes(filePath2)
        );

        var result1 = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/file-imports")
                        .file(file1))
                .andExpect(status().isAccepted())
                .andReturn();

        String responseContent1 = result1.getResponse().getContentAsString();
        FileUploadResponse uploadResponse1 = objectMapper.readValue(responseContent1, FileUploadResponse.class);

        var result2 = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/file-imports")
                        .file(file2))
                .andExpect(status().isAccepted())
                .andReturn();

        String responseContent2 = result2.getResponse().getContentAsString();
        FileUploadResponse uploadResponse2 = objectMapper.readValue(responseContent2, FileUploadResponse.class);

        await().atMost(10, SECONDS).untilAsserted(() -> {
            FileInformation fileInfo1 = fileInformationRepository.findById(uploadResponse1.getId()).orElseThrow();
            assertThat(fileInfo1.getStatus()).isEqualTo(FileStatus.SUCCESS);
        });

        await().atMost(10, SECONDS).untilAsserted(() -> {
            FileInformation fileInfo2 = fileInformationRepository.findById(uploadResponse2.getId()).orElseThrow();
            assertThat(fileInfo2.getStatus()).isEqualTo(FileStatus.SUCCESS);
        });

        FileInformation fileInfo1 = fileInformationRepository.findById(uploadResponse1.getId()).orElseThrow();
        FileInformation fileInfo2 = fileInformationRepository.findById(uploadResponse2.getId()).orElseThrow();

        assertThat(fileInfo1.getCreatedAt()).isBefore(fileInfo2.getCreatedAt());
        assertThat(fileInfo2.getStartedAt()).isAfter(fileInfo1.getFinishedAt());
        assertThat(personRepository.findAll()).isNotEmpty();
    }

    @AfterEach
    public void tearDown() {
        fileInformationRepository.deleteAll();
        personRepository.deleteAll();
    }
}