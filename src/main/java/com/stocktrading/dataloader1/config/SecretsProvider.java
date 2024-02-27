package com.stocktrading.dataloader1.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.regions.Region;

@Log4j2
@Component
@EnableCaching
@RequiredArgsConstructor
@Configuration
public class SecretsProvider {

    private final RemoteSecretsManagerClient remoteSecretsManagerClient;

    @Cacheable("alpha-vantage-api-key")
    public String getAlphaVantageApiKey() {
        log.info("Trying to obtain Alpha Vantage API Key");
        String secretName = "alpha-vantage-api-key";
        Region region = Region.of("eu-north-1");
        return remoteSecretsManagerClient.getSecretFromAws(secretName, region);
    }
}
