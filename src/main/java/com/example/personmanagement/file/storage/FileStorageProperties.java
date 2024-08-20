package com.example.personmanagement.file.storage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.upload")
@Setter
@Getter
public class FileStorageProperties {

    private String dir;
}
