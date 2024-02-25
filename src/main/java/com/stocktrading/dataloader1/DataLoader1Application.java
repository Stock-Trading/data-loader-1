package com.stocktrading.dataloader1;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootApplication
public class DataLoader1Application {


	public static void main(String[] args) {
		SpringApplication.run(DataLoader1Application.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(KafkaTemplate<String, String> template) {
		System.out.println("Sending data");
		return args -> {
			template.send("stockInfo","this is my first stockInfo event");
		};
	}


}
