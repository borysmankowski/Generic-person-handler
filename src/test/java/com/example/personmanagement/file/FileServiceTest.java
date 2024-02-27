package com.example.personmanagement.file;

import com.example.personmanagement.person.PersonRepository;
import com.example.personmanagement.person.PersonService;
import com.example.personmanagement.person.model.PersonDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class FileServiceTest {

    @Autowired
    private FileService fileService;

    @Autowired
    private FileImportRepository fileImportRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PersonService personService;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void findFileToProcess() throws IOException {
        // given
        var filePath = Paths.get("src/main/resources/files-to-import/generatedFileForTesting.csv");
        var inputStream = Files.newInputStream(filePath);
        fileService.uploadFile(inputStream, "generatedFileForTesting.csv");

        // when
        var maybeFileToProcess = fileService.findFileToProcess().join();

        // then
        assertThat(maybeFileToProcess).isNotEmpty();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void processFile() throws IOException {
        // given
        var filePath = Paths.get("src/main/resources/files-to-import/generatedFileForTesting.csv");
        var inputStream = Files.newInputStream(filePath);
        fileService.uploadFile(inputStream, "generatedFileForTesting.csv");
        var fileToProcessId = fileService.findFileToProcess().join().orElseThrow();

        // when
        var statusBeforeProcessing = fileService.getFileImportStatus(fileToProcessId);

        // then
        assertThat(statusBeforeProcessing.getBody().get("status").toString()).isEqualTo(FileStatus.PENDING.toString());

        // when
        fileService.processFile(fileToProcessId);

        // then
        var statusAfterProcessing = fileService.getFileImportStatus(fileToProcessId);
        assertThat(statusAfterProcessing.getBody().get("status").toString()).isEqualTo(FileStatus.SUCCESS.toString());

        assertThat(findByPesel("30668280097")).isNotEmpty();
        assertThat(findByPesel("38638120958")).isNotEmpty();
        assertThat(findByPesel("29911702284")).isNotEmpty();
    }

    private Optional<PersonDto> findByPesel(String pesel) {
        var result = personService.searchPersons(null, null, null, pesel, null, null, null, null, null, null, null, null, null, null, Pageable.ofSize(1));
        return result.getContent().stream().findFirst();
    }
    @BeforeEach
    public void setUp() {
        personRepository.deleteAll();
        fileImportRepository.deleteAll();
    }
}