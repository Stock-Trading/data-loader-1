package com.stocktrading.dataloader1.remote.secretManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@EnableCaching
@RequiredArgsConstructor
@Configuration
public class SecretsProvider {

    private final RemoteSecretsManagerClient remoteSecretsManagerClient;

    private static final String ALPHA_VANTAGE_API_KEY_AWS_SECRET_NAME = "alpha-vantage-api-key";
    private static final String ALPHA_VANTAGE_API_KEY_AWS_SECRET_KEY = "Alpha Vantage API Key associated with Piotr.Grochowiecki@gmail.com ";

    @Cacheable("alpha-vantage-api-key")
    public String getAlphaVantageApiKey() {
        log.info("Trying to obtain Alpha Vantage API Key");
        return remoteSecretsManagerClient.getSecret(ALPHA_VANTAGE_API_KEY_AWS_SECRET_NAME, ALPHA_VANTAGE_API_KEY_AWS_SECRET_KEY);
    }
}
