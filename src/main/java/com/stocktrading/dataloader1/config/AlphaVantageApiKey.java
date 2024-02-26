package com.stocktrading.dataloader1.config;

import lombok.extern.apachecommons.CommonsLog;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

//@CommonsLog
//@Component
//public class AlphaVantageApiKey {
//
//    public static String getSecret() {
//        log.debug("Trying to obtain Alpha Vantage API Key");
//
//        String secretName = "alpha-vantage-api-key";
//        Region region = Region.of("eu-north-1");
//
//        SecretsManagerClient client = SecretsManagerClient.builder()
//                .region(region)
//                .build();
//
//        GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
//                .secretId(secretName)
//                .build();
//
//        GetSecretValueResponse getSecretValueResponse;
//
//        try {
//            getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
//        } catch (Exception e) {
//            log.error("Exception has been thrown and captured while obtaining Alpha Vantage API Key: " + e.getMessage());
//            throw e;
//        }
//        return getSecretValueResponse.secretString();
//
//    }
//}
