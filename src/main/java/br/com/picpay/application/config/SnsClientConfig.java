package br.com.picpay.application.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.client.config.SdkAdvancedClientOption;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;

import java.net.URI;

@Configuration
@RequiredArgsConstructor
public class SnsClientConfig {

    @Value("${spring.cloud.aws.sns.endpoint.http}")
    private String snsEndpoint;

    @Bean
    public SnsClient snsClient() {
        return SnsClient.builder()
                .endpointOverride(URI.create(snsEndpoint))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("key", "key")))
                .region(Region.US_EAST_1)
                .overrideConfiguration(clientConfiguration ->
                        clientConfiguration.putAdvancedOption(
                                SdkAdvancedClientOption.DISABLE_HOST_PREFIX_INJECTION, true))
                .build();
    }
}
