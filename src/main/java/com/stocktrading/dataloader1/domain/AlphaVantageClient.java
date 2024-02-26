package com.stocktrading.dataloader1.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@RequiredArgsConstructor
public class AlphaVantageClient {

    private final RestClient restClient;

    public TimeSeriesDaily getTimeSeriesDaily() throws RestClientException {
        TimeSeriesDaily timeSeriesDaily = restClient.get()
                .uri("https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=IBMFSDFD&apikey=ZTX7QFX5EKRFFR0L")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(TimeSeriesDaily.class);

        if (timeSeriesDaily == null || timeSeriesDaily.getMetaData() == null || timeSeriesDaily.getDayParametersMap() == null) {
            throw new RestClientException("Received empty payload. Check request parameters");
        }
        return timeSeriesDaily;
    }

}
