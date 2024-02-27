package com.example.personmanagement.file;

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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testFileLoaderEndpoint() throws Exception {
        // given
        Path filePath = Paths.get("src/main/resources/files-to-import/generatedFileForTesting.csv");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "testFile.csv",
                MediaType.TEXT_PLAIN_VALUE,
                Files.readAllBytes(filePath)
        );

        var result = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/file-imports")
                .file(file));

        result.andExpect(MockMvcResultMatchers.status().isOk());
    }


}