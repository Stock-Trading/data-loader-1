package com.stocktrading.dataloader1;

import com.stocktrading.dataloader1.eventPublisher.StockDataEventPublisher;
import com.stocktrading.dataloader1.remoteClient.dataProvider.RemoteDataProviderClient;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@Log4j2
@SpringBootApplication
@AllArgsConstructor
public class DataLoader1Application {

	private RemoteDataProviderClient remoteDataProviderClient;
	private StockDataEventPublisher stockDataEventPublisher;

	public static void main(String[] args) {
		SpringApplication.run(DataLoader1Application.class, args);
	}

}


//TODO w pakiecie Domain powinien być serwis ze schedulerem regularnie wywołującym żądanie do zewnętrznego API pobierających dane
// różne zewnętrzne API będą pobierać te same dane, ale w JSON (czy cokolwiek innego) może być inaczej skonstruowany
// model domeny jest jeden, modele DTO powinny mapować do jednego modelu, który powinien być już ustalony
// schemat budowy aplikacji jak w poprzednim projekcie