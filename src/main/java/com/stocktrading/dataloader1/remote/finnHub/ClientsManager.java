package com.stocktrading.dataloader1.remote.finnHub;

import com.stocktrading.dataloader1.domain.service.FinancialInstrumentService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
@Getter
@Component
@RequiredArgsConstructor
class ClientsManager {

    private final FinancialInstrumentService financialInstrumentService;
    private final OkHttpClientFactory clientFactory;
    private final FinnHubApiHandlerFactory handlerFactory;

    private final ConcurrentHashMap<WebSocket, List<String>> webSocketAndFinancialInstrumentsListConcurrentHashMap = new ConcurrentHashMap<>();

    private final static String FINNHUB_BASE_URL = "wss://ws.finnhub.io?token=";
    private final static List<String> TOKEN_LIST = List.of("col6umhr01qkduilq8r0col6umhr01qkduilq8rg", "cnli7c9r01qk2u6r38j0cnli7c9r01qk2u6r38jg");
    private final static int NUMBER_OF_INSTRUMENTS_PER_CLIENT = 5;

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
                    .url(FINNHUB_BASE_URL + ClientsManager.TOKEN_LIST.get(clientNumber))
                    .build();

            WebSocket webSocket = client.newWebSocket(request, handlerFactory.getFinnHubApiHandler(listOfSymbolsToSubscribe));
            webSocketAndFinancialInstrumentsListConcurrentHashMap.put(webSocket, listOfSymbolsToSubscribe);

            log.info("Created web socket client {}. Thread: {}. Instruments subscribed on startup: {}", webSocket.toString(), Thread.currentThread().threadId(), listOfSymbolsToSubscribe);
        };

        Thread webSocketFinnHubClientThread = new Thread(clientConfigurationRunnable);
        webSocketFinnHubClientThread.start();
    }

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
    }

    private int calculateNumberOfRequiredClientsOnStartup() {
        int numberOfAllInstruments = financialInstrumentService.getAllSymbolsOfCurrentlySubscribed().size();
        return (numberOfAllInstruments + NUMBER_OF_INSTRUMENTS_PER_CLIENT - 1) / NUMBER_OF_INSTRUMENTS_PER_CLIENT;
    }

}
