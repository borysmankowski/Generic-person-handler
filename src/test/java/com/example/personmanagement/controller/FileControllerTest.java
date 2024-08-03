package com.example.personmanagement.controller;

import com.example.personmanagement.model.file.FileInformation;
import com.example.personmanagement.model.file.FileStatus;
import com.example.personmanagement.model.person.Person;
import com.example.personmanagement.repository.FileInformationRepository;
import com.example.personmanagement.repository.PersonRepository;
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
                .file(file));

        result.andExpect(status().isAccepted());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @BatchSize(size = 3)
    void testFileLoaderEndpoint_ShouldHaveProcessedRecords() throws Exception {
        // given
        String uniqueFileName = "testFile-" + UUID.randomUUID() + ".csv";
        Path filePath = Paths.get("files-to-import/generatedFileForTesting.csv");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                uniqueFileName,
                MediaType.TEXT_PLAIN_VALUE,
                Files.readAllBytes(filePath)
        );

        var result = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/file-imports")
                .file(file));

        result.andExpect(status().isAccepted());

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
            assertThat(persons).isNotEmpty();
        });
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @BatchSize(size = 1)
    void testFileLoaderEndpoint_ShouldRollbackDueToDuplicates() throws Exception {
        // given
        String uniqueFileName = "testFile-" + UUID.randomUUID() + ".csv";
        Path filePath = Paths.get("files-to-import/generatedFileForTestingDuplicatedPesel.csv");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                uniqueFileName,
                MediaType.TEXT_PLAIN_VALUE,
                Files.readAllBytes(filePath)
        );

        var result = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/file-imports")
                .file(file));

        result.andExpect(status().isAccepted());

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
    }

    @AfterEach
    public void setUp() {
        fileInformationRepository.deleteAll();
        personRepository.deleteAll();
    }
}