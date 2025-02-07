package br.com.picpay.application.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.client.config.SdkAdvancedClientOption;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;


import java.net.URI;

@Configuration
public class SesClientConfig {

    @Value("${spring.cloud.aws.ses.endpoint}")
    private String sesEndpoint;

    @Bean
    public SesClient sesClient() {
        return SesClient.builder()
                .endpointOverride(URI.create(sesEndpoint))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("key", "key")))
                .region(Region.US_EAST_1)
                .overrideConfiguration(clientConfiguration ->
                        clientConfiguration.putAdvancedOption(
                                SdkAdvancedClientOption.DISABLE_HOST_PREFIX_INJECTION, true))
                .build();
    }
}
