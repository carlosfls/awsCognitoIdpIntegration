package com.mydomain.cognitoclient.cognito;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CognitoClient {

    @Value("${region}")
    private String region;

    private final Log logger = LogFactory.getLog(CognitoClient.class);

    @Bean
    public AWSCognitoIdentityProvider createCognitoClient(){
        logger.info("creating the cognito client...");
        logger.info("cognito client created successfully");
        return AWSCognitoIdentityProviderClientBuilder
                .standard()
                .withRegion(region)
                .build();
    }
}
