package com.stocktrading.dataloader1;

import com.stocktrading.dataloader1.domain.AlphaVantageClient;
import lombok.AllArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.client.RestClientException;

@CommonsLog
@SpringBootApplication
@AllArgsConstructor
public class DataLoader1Application {

	private AlphaVantageClient alphaVantageClient;

	public static void main(String[] args) {
		SpringApplication.run(DataLoader1Application.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(KafkaTemplate<String, String> template) {
		String topic = "stockInfo";
		String data = "this is my first stockInfo event";
		log.debug("Sending data to the topic: " + topic + "; data: " + data);
		return args -> {
			template.send(topic, data);
			log.info("Message has been sent to stockInfo topic: ");
		};
	}

	@Bean
	CommandLineRunner printTimeSeriesDailyOnStartup() {
		return args -> {
			log.info("Calling Alpha Vantage for Time Series Daily for IBM");
			try {
				log.info(alphaVantageClient.getTimeSeriesDaily());
			} catch (RestClientException restClientException) {
				log.error("Error has occurred while trying to obtain Time Series Daily: " + restClientException.getMessage());
			}

		};
	}

}
