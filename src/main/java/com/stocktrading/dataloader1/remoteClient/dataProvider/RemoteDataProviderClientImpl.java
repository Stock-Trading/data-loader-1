package com.stocktrading.dataloader1.remoteClient.dataProvider;

import com.stocktrading.dataloader1.remoteClient.secretManager.SecretsProvider;
import lombok.RequiredArgsConstructor;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@RequiredArgsConstructor
@Log4j2
class RemoteDataProviderClientImpl implements RemoteDataProviderClient {

    private final RestClient restClient;
    private final SecretsProvider secretsProvider;

    @Override
    public TimeSeriesDaily getTimeSeriesDailyFromAlphaVantage(String stockSymbol) throws RestClientException {

        TimeSeriesDaily timeSeriesDaily = restClient.get()
                .uri(getAlphaVantageUri(AlphaVantageFunction.TIME_SERIES_DAILY, stockSymbol))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(TimeSeriesDaily.class);

        log.info("Trying to obtain Time series (daily) data for {} from Alpha Vantage", stockSymbol);

        if (timeSeriesDaily == null || timeSeriesDaily.metaData() == null || timeSeriesDaily.dayParametersMap() == null) {
            throw new RestClientException("Received empty payload. Check request parameters");
        }

        return timeSeriesDaily;
    }

    private String getAlphaVantageUri(AlphaVantageFunction function, String symbol) {

        final String baseUri = "https://www.alphavantage.co/query?function=";
        final String alphaVantageApiKey = secretsProvider.getAlphaVantageApiKey();

        StringBuilder uri = new StringBuilder();
        uri.append(baseUri)
                .append(function)
                .append("&symbol=")
                .append(symbol.toUpperCase())
                .append("&apikey=")
                .append(alphaVantageApiKey);
        return uri.toString();
    }

}