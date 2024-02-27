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

    @Override
    public String getSecretFromAws(String secretName, String secretDescription, Region awsRegion) {
        log.info("Obtaining secret {} from AWS Secret Manager", secretName);
        try (SecretsManagerClient client = SecretsManagerClient.builder()
                .region(awsRegion)
                .build()) {
            GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                    .secretId(secretName)
                    .build();

            GetSecretValueResponse getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
            String secretValue = getSecretFromKeyValueJsonPair(getSecretValueResponse.secretString(), secretDescription);
            log.info("Successfully obtained secret {} from AWS Secret Manager", secretName);
            return secretValue;
        } catch (Exception e) {
            log.error("Could not obtain secret {} from AWS Secret Manager. Exception has been thrown {}", secretName, e);
            throw new AwsSecretManagerRuntimeException("Could not obtain a secret " + secretName +
                    " from AWS Secret Manager. Exception has been thrown " + e);
        }
    }

    private String getSecretFromKeyValueJsonPair(String keyValueJsonPair, String secretDescription) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.readTree(keyValueJsonPair);
        return node.get(secretDescription).asText();
    }
}
