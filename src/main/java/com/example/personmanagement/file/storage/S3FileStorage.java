package com.example.personmanagement.file.storage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "file-storage", name = "solution", havingValue = "s3")
public class S3FileStorage implements FileStorage {

    private final AmazonS3 amazonS3;

    @Value("${aws.bucketName}")
    private String bucketName;

    @Override
    public String save(InputStream inputsStream, String originalFilename, long byteSize) {
        String uniqueFilename = System.currentTimeMillis() + "_" + originalFilename;
        var s3metadata = new ObjectMetadata();
        s3metadata.setContentLength(byteSize);
        amazonS3.putObject(bucketName, uniqueFilename, inputsStream, s3metadata);
        return uniqueFilename;
    }

    @Override
    public BufferedReader load(String fileName) {
        S3Object getObjectResult = amazonS3.getObject("person-management-bucket", fileName);
        S3ObjectInputStream fileInputStream = getObjectResult.getObjectContent();
        return new BufferedReader(new InputStreamReader(fileInputStream));
    }

}