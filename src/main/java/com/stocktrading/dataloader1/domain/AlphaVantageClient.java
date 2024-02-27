package com.stocktrading.dataloader1.domain;

import com.stocktrading.dataloader1.config.SecretsProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@RequiredArgsConstructor
@CommonsLog
public class AlphaVantageClient {

    private final RestClient restClient;
    private final SecretsProvider secretsProvider;

    public TimeSeriesDaily getTimeSeriesDaily() throws RestClientException {

        String alphaVantageApiKey = secretsProvider.getAlphaVantageApiKey();
        TimeSeriesDaily timeSeriesDaily = restClient.get()
                .uri("https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=IBM&apikey=" + alphaVantageApiKey)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(TimeSeriesDaily.class);
        log.info("Trying to obtain Time series (daily) data for IBM from Alpha Vantage");
        if (timeSeriesDaily == null || timeSeriesDaily.getMetaData() == null || timeSeriesDaily.getDayParametersMap() == null) {
            throw new RestClientException("Received empty payload. Check request parameters");
        }
        return timeSeriesDaily;
    }

}
