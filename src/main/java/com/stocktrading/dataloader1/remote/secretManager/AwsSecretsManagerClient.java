package com.stocktrading.dataloader1.remote.secretManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stocktrading.dataloader1.domain.ports.RemoteSecretsManagerClient;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

@Component
@Log4j2
class AwsSecretsManagerClient implements RemoteSecretsManagerClient {

    private static final String FINN_HUB_API_KEY_ONE_AWS_SECRET_NAME = "FinnHubApiKeyOne";
    private static final String FINN_HUB_API_KEY_ONE_AWS_SECRET_KEY = "finnHubApiKeyOne";

    @Override
    public String getFirstFinnHubApiKey() {
        log.info("Obtaining first FinnHub API Key from AWS Secret Manager");
        try (SecretsManagerClient client = SecretsManagerClient.create()) {
            GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                    .secretId(FINN_HUB_API_KEY_ONE_AWS_SECRET_NAME)
                    .build();

            GetSecretValueResponse getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
            String secretValue = getSecretFromKeyValueJsonPair(getSecretValueResponse.secretString(), FINN_HUB_API_KEY_ONE_AWS_SECRET_KEY);
            log.info("Successfully obtained first FinnHub API Key from AWS Secret Manager");
            return secretValue;
        } catch (Exception e) {
            log.error("Could not obtain secret {} from AWS Secret Manager. Exception has been thrown {}", FINN_HUB_API_KEY_ONE_AWS_SECRET_NAME, e);
            throw new SecretManagerClientRuntimeException("Could not obtain secret \"" + FINN_HUB_API_KEY_ONE_AWS_SECRET_NAME +
                    "\" from AWS Secret Manager. Exception has been thrown " + e);
        }
    }

    private String getSecretFromKeyValueJsonPair(String keyValueJsonPair, String secretDescription) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.readTree(keyValueJsonPair);
        return node.get(secretDescription).asText();
    }
}
