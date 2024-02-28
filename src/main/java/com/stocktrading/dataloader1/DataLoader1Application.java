package com.stocktrading.dataloader1;

import com.stocktrading.dataloader1.eventPublisher.StockDataEventPublisher;
import com.stocktrading.dataloader1.remoteClient.dataProvider.RemoteDataProviderClient;
import com.stocktrading.dataloader1.remoteClient.dataProvider.TimeSeriesDaily;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.client.RestClientException;

@Log4j2
@SpringBootApplication
@AllArgsConstructor
public class DataLoader1Application {

	private RemoteDataProviderClient remoteDataProviderClient;
	private StockDataEventPublisher stockDataEventPublisher;

	public static void main(String[] args) {
		SpringApplication.run(DataLoader1Application.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner() {
//		String topic = "stockInfo";
//		String data = "this is my first stockInfo event";
//		log.debug("Trying to send data \"{}\" to the topic \"{}\"", data, topic);
		log.info("Trying to publish data");
		return args -> {
			log.info("I am part of args. Can you see me?");
			stockDataEventPublisher.publishTimeSeriesDailyEvent("ibm");
		};
	}

	@Bean
	CommandLineRunner printTimeSeriesDailyOnStartup() {
		return args -> {
			log.info("Calling Alpha Vantage for Time Series Daily for IBM");
			try {
				String stockSymbol = "ibm";
				TimeSeriesDaily timeSeriesDailyIbm = remoteDataProviderClient.getTimeSeriesDailyFromAlphaVantage(stockSymbol);
				log.info("Received data for stock {} from Alpha Vantage. Excerpt from payload: {}", stockSymbol, timeSeriesDailyIbm.toString().substring(0, 200));
			} catch (RestClientException restClientException) {
				log.error("Error has occurred while trying to obtain Time Series Daily: " + restClientException.getMessage());
			}

		};
	}

}
