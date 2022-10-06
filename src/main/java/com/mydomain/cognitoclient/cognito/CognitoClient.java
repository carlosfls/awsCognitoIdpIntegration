package com.mydomain.cognitoclient.cognito;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CognitoClient {

    @Value("${accessKey}")
    private String accessKey;

    @Value("${secretKey}")
    private String secretKey;

    private final String region = "sa-east-1";

    private final Log logger = LogFactory.getLog(CognitoClient.class);

    @Bean
    public AWSCognitoIdentityProvider createCognitoClient(){
        logger.info("creating the cognito client...");
        var cred = new BasicAWSCredentials(accessKey,secretKey);
        var credProvider = new AWSStaticCredentialsProvider(cred);
        logger.info("cognito client created successfully");
        return AWSCognitoIdentityProviderClientBuilder
                .standard()
                .withCredentials(credProvider)
                .withRegion(region)
                .build();
    }
}
