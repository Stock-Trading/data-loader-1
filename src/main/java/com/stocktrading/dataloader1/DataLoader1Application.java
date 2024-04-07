package com.stocktrading.dataloader1;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Log4j2
@SpringBootApplication
@AllArgsConstructor
public class DataLoader1Application {

    public static void main(String[] args) {
        SpringApplication.run(DataLoader1Application.class, args);
    }
}


//TODO w pakiecie Domain powinien być serwis ze schedulerem regularnie wywołującym żądanie do zewnętrznego API pobierających dane
// różne zewnętrzne API będą pobierać te same dane, ale w JSON (czy cokolwiek innego) może być inaczej skonstruowany
// model domeny jest jeden, modele DTO powinny mapować do jednego modelu, który powinien być już ustalony
// schemat budowy aplikacji jak w poprzednim projekcie


//TODO tworzyć ręcznie klientów websocketowych na żądanie. Domain musi mieć metody kontrolujące tworzenie i niszczenie
// instancji klientów
// Metoda tworząca nowe instancje powinna przyjmować nazwę instrumentu finansowego (np. symbol firmy lub parę walutową)
// do których klient ma subskrybować na jednym websocketowym połączniu. Przyjmujemy, że jedno połączenie obsługuje
// do pięciu instrumentów (np. 17 zasubskrybowanych instrumentów oznacza 4 połączenia web socketowe, 3x5+2 instrumentów)
// Każde połączenie ma swój wątek.

//TODO zastanowić się czy to domain ma obsługiwać liczbę klientów czy warstwa klienta (warstwa transportowa) sama to kontroluje

//TODO zobaczyć czy jeden klient web socketowy może nadawać na wiele wątków

//https://docs.spring.io/spring-framework/docs/4.3.5.RELEASE/spring-framework-reference/html/websocket.html#websocket-stomp-configuration-performance

//TODO 26.03.2024 na razie nie przejmować się użytkownikiem, data loader powinien wrzucać wszystko co może do kafki, np. 20 instrumentów z 4 klientów

//TODO Klient AWS Secret Managera powinien także być zwrócony ku domenie przez interfejs (obecnie nie jest)

//TODO do FinnHub przy wielu klientach nadpisać header User-Agent, żeby spróbować uniknąć bana