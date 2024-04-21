package com.stocktrading.dataloader1.remote.secretManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

@Component
@Log4j2
class AwsSecretsManagerClient implements RemoteSecretsManagerClient {

    /**
     * @param secretName Name of the secret object in AWS Secret Manager
     * @param secretKey Key for the secret value stored in AWS SM (secret is an AWS SM object stored (in this case) as key-value pair and send in JSON format)
     * @return secret value
     */
    @Override
    public String getSecret(String secretName, String secretKey) {
        log.info("Obtaining secret {} from AWS Secret Manager", secretName);
        try (SecretsManagerClient client = SecretsManagerClient.create()) {
            GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                    .secretId(secretName)
                    .build();

            GetSecretValueResponse getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
            String secretValue = getSecretFromKeyValueJsonPair(getSecretValueResponse.secretString(), secretKey);
            log.info("Successfully obtained secret {} from AWS Secret Manager", secretName);
            return secretValue;
        } catch (Exception e) {
            log.error("Could not obtain secret {} from AWS Secret Manager. Exception has been thrown {}", secretName, e);
            throw new SecretManagerClientRuntimeException("Could not obtain secret \"" + secretName +
                    "\" from AWS Secret Manager. Exception has been thrown " + e);
        }
    }

    private String getSecretFromKeyValueJsonPair(String keyValueJsonPair, String secretDescription) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.readTree(keyValueJsonPair);
        return node.get(secretDescription).asText();
    }
}
