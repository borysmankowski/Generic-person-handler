//package com.example.personmanagement.file;
//
//import com.amazonaws.client.builder.AwsClientBuilder;
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.AmazonS3ClientBuilder;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class AwsS3ClientConfiguration {
//
//    private final String serviceEndpoint;
//    private final String signingRegion;
//
//    public AwsS3ClientConfiguration(
//            @Value("${aws.serviceEndpoint}") String serviceEndpoint,
//            @Value("${aws.signingRegion}") String signingRegion
//    ) {
//        this.serviceEndpoint = serviceEndpoint;
//        this.signingRegion = signingRegion;
//    }
//
//    @Bean
//    public AmazonS3 amazonS3() {
//        AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(
//                serviceEndpoint,
//                signingRegion
//        );
//
//        return AmazonS3ClientBuilder.standard()
//                .withEndpointConfiguration(endpointConfiguration)
//                .withPathStyleAccessEnabled(true)
//                .build();
//    }
//
//}