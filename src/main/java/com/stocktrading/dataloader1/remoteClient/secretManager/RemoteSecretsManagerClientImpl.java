package com.stocktrading.dataloader1.remoteClient.secretManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

@Component
@Log4j2
class RemoteSecretsManagerClientImpl implements RemoteSecretsManagerClient {

    private static final String API_KEY_DESCRIPTION_FROM_AWS_CONSOLE = "Alpha Vantage API Key associated with Piotr.Grochowiecki@gmail.com ";

    @Override
    public String getSecretFromAws(String secretName, Region awsRegion) {
        try (SecretsManagerClient client = SecretsManagerClient.builder()
                .region(awsRegion)
                .build()) {
            GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                    .secretId(secretName)
                    .build();

            log.info("Obtaining secret {} from AWS Secret Manager", secretName);

            GetSecretValueResponse getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
            return getApiKeyFromKeyValueJsonPair(getSecretValueResponse.secretString());
        } catch (Exception e) {
            log.error("Could not obtain a secret {} from AWS Secret Manager. Exception has been thrown {}", secretName, e);
            throw new AwsSecretManagerRuntimeException("Could not obtain a secret " + secretName +
                    " from AWS Secret Manager. Exception has been thrown " + e);
        }
    }

    private String getApiKeyFromKeyValueJsonPair(String keyValueJsonPair) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.readTree(keyValueJsonPair);
        return node.get(API_KEY_DESCRIPTION_FROM_AWS_CONSOLE).asText();
    }
}
