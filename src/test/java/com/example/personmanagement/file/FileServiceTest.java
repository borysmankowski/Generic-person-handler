package com.example.personmanagement.file;

import com.example.personmanagement.person.PersonRepository;
import com.example.personmanagement.person.PersonService;
import com.example.personmanagement.person.model.PersonDto;
import com.example.personmanagement.person.model.SearchCriteria;
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
import java.util.List;
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
        fileService.uploadFile(inputStream, "generatedFileForTesting.csv",Files.size(filePath));

        // when
        var maybeFileToProcess = fileService.findFileToProcess();

        // then
        assertThat(maybeFileToProcess).isNotEmpty();
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void processFile() throws IOException {
        // given
        var filePath = Paths.get("src/main/resources/files-to-import/generatedFileForTesting.csv");
        var inputStream = Files.newInputStream(filePath);

        fileService.uploadFile(inputStream, "generatedFileForTesting.csv",Files.size(filePath));
        var fileToProcessId = fileService.findFileToProcess().orElseThrow();

        // when
        var statusBeforeProcessing = fileService.getFileImportStatus(fileToProcessId);

        // then
        assertThat(statusBeforeProcessing.getStatus().toString()).isEqualTo(FileStatus.PENDING.toString());

        // when
        fileService.processFile(fileToProcessId);

        // then
        var statusAfterProcessing = fileService.getFileImportStatus(fileToProcessId);
        assertThat(statusAfterProcessing.getStatus().toString()).isEqualTo(FileStatus.SUCCESS.toString());

        assertThat(findByPesel("70081539775")).isNotEmpty();
        assertThat(findByPesel("90122199526")).isNotEmpty();
        assertThat(findByPesel("51010932991")).isNotEmpty();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void processFileWithDuplicatedPesel() throws IOException {
        // given
        var filePath = Paths.get("src/main/resources/files-to-import/generatedFileForTestingDuplicatedPesel.csv");
        var inputStream = Files.newInputStream(filePath);
        fileService.uploadFile(inputStream, "generatedFileForTesting.csv",Files.size(filePath));
        var fileToProcessId = fileService.findFileToProcess().orElseThrow();

        // when
        var statusBeforeProcessing = fileService.getFileImportStatus(fileToProcessId);

        // then
        assertThat(statusBeforeProcessing.getStatus().toString()).isEqualTo(FileStatus.PENDING.toString());

        // when
        fileService.processFile(fileToProcessId);

        // then
        var statusAfterProcessing = fileService.getFileImportStatus(fileToProcessId);
        assertThat(statusAfterProcessing.getStatus().toString()).isEqualTo(FileStatus.FAILED.toString());

        assertThat(findByPesel("70081539775")).isNotEmpty();
        assertThat(findByPesel("90122199526")).isNotEmpty();
        assertThat(findByPesel("51010932991")).isEmpty();
    }


    private Optional<PersonDto> findByPesel(String pesel) {
        SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setKey("pesel");
        searchCriteria.setOperation("eq");
        searchCriteria.setValue(pesel);

        var searchCriteriaList = List.of(searchCriteria);

        var result = personService.searchPersons(searchCriteriaList, Pageable.unpaged());
        return result.getContent().stream().findFirst();
    }
    @BeforeEach
    public void setUp() {
        personRepository.deleteAll();
        fileImportRepository.deleteAll();
    }
}