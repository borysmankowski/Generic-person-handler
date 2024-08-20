package com.example.personmanagement.file.storage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "file-queue-processor")
@Setter
@Getter
public class FileBatchProcessingProperties {

    private long batchSize;
}
