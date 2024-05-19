package com.stocktrading.dataloader1.remote.finnHub;

import com.stocktrading.dataloader1.domain.service.FinancialInstrumentService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Component
@AllArgsConstructor
class ClientsManager {

    private final FinancialInstrumentService financialInstrumentService;
    private final OkHttpClientFactory clientFactory;
    private final FinnHubApiHandlerFactory handlerFactory;

    private final static int NUMBER_OF_INSTRUMENTS_PER_CLIENT = 5;

    void provideWebSocketClients() throws InterruptedException {
        final int numberOfClientsToCreate = calculateNumberOfRequiredClientsOnStartup();
        final List<List<String>> listOfListOfSymbolsForGivenClientToSubscribe = constructListOfListOfFinancialInstrumentSymbolsForMultipleClientsToSubscribeOnStartup();

//        List<WebSocket> webSocketList = new ArrayList<>();
        for (int i = 0; i < numberOfClientsToCreate; i++) {
            if (i == 0) {
                final int tempIterator = i;
                Runnable runnable = () -> {
                    OkHttpClient client = clientFactory.okHttpClient();
                    Request request = new Request.Builder()
                            .url("wss://ws.finnhub.io?token=col6umhr01qkduilq8r0col6umhr01qkduilq8rg")
                            //token for ggroch95@gmail.com
                            .build();

                    WebSocket webSocket = client.newWebSocket(request, handlerFactory.getFinnHubApiHandler(listOfListOfSymbolsForGivenClientToSubscribe.get(tempIterator)));
                    log.info("Created web socket client {}. Thread: {}", webSocket.toString(), Thread.currentThread().threadId());
                    try {
                        log.info("going to sleep for 5 sec");
                        Thread.sleep(Duration.ofSeconds(5));
                        log.info("woke up after 5 sec");
                    } catch (InterruptedException ignored) {

                    }

                    webSocket.cancel();

                    try {
                        log.info("going to sleep for another 5 sec");
                        Thread.sleep(Duration.ofSeconds(5));
                        log.info("woke up after another 5 sec");
                    } catch (InterruptedException ignored) {

                    }

                    WebSocket webSocketRecreated = client.newWebSocket(request, handlerFactory.getFinnHubApiHandler(listOfListOfSymbolsForGivenClientToSubscribe.get(tempIterator)));
                    log.info("Created web socket client {}. Thread: {}", webSocketRecreated.toString(), Thread.currentThread().threadId());

                };
                Thread thread = new Thread(runnable);
                thread.start();
            }
            if (i == 1) {
                final int tempIterator2 = i;
                Runnable runnable2 = () -> {
                    OkHttpClient client = clientFactory.okHttpClient();
                    Request request = new Request.Builder()
                            .url("wss://ws.finnhub.io?token=cnli7c9r01qk2u6r38j0cnli7c9r01qk2u6r38jg")
                            //token for piotrgrochowiecki@gmail.com
                            .build();

                    WebSocket webSocket = client.newWebSocket(request, handlerFactory.getFinnHubApiHandler(listOfListOfSymbolsForGivenClientToSubscribe.get(tempIterator2)));
                    log.info("Created web socket client {}. Thread: {}", webSocket.toString(), Thread.currentThread().threadId());
                };
                Thread thread2 = new Thread(runnable2);
                thread2.start();
            }
        }
//        return webSocketList;
    }

    //TODO najpierw napisać ładne sterowanie, potem zająć się obsługą błędów (np. brak internetu, za dużo instrumentów do subskrybcji)
    // sterowanie: musi być mechanizm nasłuchujący jakie żądanie (sub/unsub) i jakiego instrumentu przychodzi, np. websocket i lista jego subskrybcji w mapie.
    // funkcja szuka odpowiedniego instrumentu i go unsubuje lub jeśli jest żądanie sub, to subskrybuje

    @PostConstruct
    public void init() throws InterruptedException {
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
