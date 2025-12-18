package com.stocktrading.dataloader1.remote.finnHub;

import com.stocktrading.dataloader1.domain.ports.RemoteSecretsManagerClient;
import com.stocktrading.dataloader1.domain.service.FinancialInstrumentService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
@Getter
@Component
@RequiredArgsConstructor
class ClientsManager {

    private final FinancialInstrumentService financialInstrumentService;
    private final OkHttpClientFactory clientFactory;
    private final FinnHubApiHandlerFactory handlerFactory;
    private final RemoteSecretsManagerClient remoteSecretsManagerClient;

    private final ConcurrentHashMap<WebSocket, List<String>> webSocketAndFinancialInstrumentsListConcurrentHashMap = new ConcurrentHashMap<>();
    //TODO mapę jako pole powinna być zastąpiona oddzielnym serwisem z metodami, np. dodania nowego websocketu, znajdz web socket, usun websocket. Taki serwis powinien przechowywać aktualny stan

    private final static String FINNHUB_BASE_URL = "wss://ws.finnhub.io?token=";

    @Value("${max_number_of_financial_instruments_per_client}")
    private Integer NUMBER_OF_INSTRUMENTS_PER_CLIENT;

    @Value("${number_of_available_finnhub_tokens}")
    private Integer NUMBER_OF_AVAILABLE_FINNHUB_TOKENS;

    void provideWebSocketClients() {
        final int numberOfClientsToCreate = calculateNumberOfRequiredClientsOnStartup();
        final List<List<String>> listOfListOfSymbolsForGivenClientToSubscribe = constructListOfListOfFinancialInstrumentSymbolsForMultipleClientsToSubscribeOnStartup();

        for (int i = 0; i < numberOfClientsToCreate; i++) {
            createFinnHubClient(i, listOfListOfSymbolsForGivenClientToSubscribe);
        }
    }

    private void createFinnHubClient(int clientNumber, List<List<String>> listOfListOfSymbolsForGivenClientToSubscribe) {
        Runnable clientConfigurationRunnable = () -> {
            List<String> listOfSymbolsToSubscribe = listOfListOfSymbolsForGivenClientToSubscribe.get(clientNumber);
            OkHttpClient client = clientFactory.okHttpClient();
            Request request = new Request.Builder()
                    .url(FINNHUB_BASE_URL + remoteSecretsManagerClient.getFinnHubApiKeys().get(clientNumber))
                    .build();

            WebSocket webSocket = client.newWebSocket(request, handlerFactory.getFinnHubApiHandler(listOfSymbolsToSubscribe));
            webSocketAndFinancialInstrumentsListConcurrentHashMap.put(webSocket, listOfSymbolsToSubscribe);

            log.info("Created web socket client {}. Thread: {}. Instruments subscribed on startup: {}", webSocket.toString(), Thread.currentThread().threadId(), listOfSymbolsToSubscribe);
        };

        Thread webSocketFinnHubClientThread = new Thread(clientConfigurationRunnable);
        webSocketFinnHubClientThread.start();
    }
    //TODO 21.05.2024:

    //TODO najpierw napisać ładne sterowanie, potem zająć się obsługą błędów (np. brak internetu, za dużo instrumentów do subskrybcji)
    // sterowanie: musi być mechanizm nasłuchujący jakie żądanie (sub/unsub) i jakiego instrumentu przychodzi, np. websocket i lista jego subskrybcji w mapie.
    // funkcja szuka odpowiedniego instrumentu i go unsubuje lub jeśli jest żądanie sub, to subskrybuje

    @PostConstruct
    public void init() {
        provideWebSocketClients();
    }

    private List<List<String>> constructListOfListOfFinancialInstrumentSymbolsForMultipleClientsToSubscribeOnStartup() {
        final List<String> listOfAllSymbols = financialInstrumentService.getAllSymbolsOfCurrentlySubscribed();
        List<List<String>> listOfListOfSymbolsForGivenClientToSubscribe = new ArrayList<>();

        for (int i = 0; i < listOfAllSymbols.size(); i += NUMBER_OF_INSTRUMENTS_PER_CLIENT) {
            int end = Math.min(i + NUMBER_OF_INSTRUMENTS_PER_CLIENT, listOfAllSymbols.size());
            List<String> sublist = listOfAllSymbols.subList(i, end);
            listOfListOfSymbolsForGivenClientToSubscribe.add(sublist);
        }
        return listOfListOfSymbolsForGivenClientToSubscribe;
    } //TODO pomyśleć nad zamianą tego. Lista list jest zła!

    private int calculateNumberOfRequiredClientsOnStartup() {
        int numberOfAllInstruments = financialInstrumentService.getAllSymbolsOfCurrentlySubscribed().size();
        return Math.min(((numberOfAllInstruments + NUMBER_OF_INSTRUMENTS_PER_CLIENT - 1) / NUMBER_OF_INSTRUMENTS_PER_CLIENT), NUMBER_OF_AVAILABLE_FINNHUB_TOKENS);
    }

}
