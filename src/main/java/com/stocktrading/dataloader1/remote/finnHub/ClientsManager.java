package com.stocktrading.dataloader1.remote.finnHub;

import com.stocktrading.dataloader1.domain.service.FinancialInstrumentService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import org.springframework.stereotype.Component;

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

    void provideWebSocketClients() {
        List<String> listOfAllSymbols = financialInstrumentService.getAllSymbolsOfCurrentlySubscribed();

        int numberOfAllInstruments = listOfAllSymbols.size();
        int numberOfClientsToCreate;

        if ((numberOfAllInstruments % NUMBER_OF_INSTRUMENTS_PER_CLIENT) == 0) {
            numberOfClientsToCreate = Math.divideExact(numberOfAllInstruments, NUMBER_OF_INSTRUMENTS_PER_CLIENT);
        } else {
            numberOfClientsToCreate = Math.divideExact(numberOfAllInstruments, NUMBER_OF_INSTRUMENTS_PER_CLIENT) + 1;
        }

        List<List<String>> listOfListOfSymbolsForGivenClientToSubscribe = new ArrayList<>();

        for (int i = 1; i <= numberOfAllInstruments; i++) {
            if (i % NUMBER_OF_INSTRUMENTS_PER_CLIENT != 0) {
                continue;
            }
            List<String> listOfSymbolsForGivenClientToSubscribe;
            listOfSymbolsForGivenClientToSubscribe = listOfAllSymbols.subList(i - 5, i);
            listOfListOfSymbolsForGivenClientToSubscribe.add(listOfSymbolsForGivenClientToSubscribe);
        }

        int numberOfLastSymbolsToAdd = numberOfAllInstruments % NUMBER_OF_INSTRUMENTS_PER_CLIENT;
        if (numberOfLastSymbolsToAdd != 0) {
            List<String> lastListOfSymbolsToSubscribe = listOfAllSymbols.subList(listOfAllSymbols.size() - numberOfLastSymbolsToAdd, listOfAllSymbols.size());
            listOfListOfSymbolsForGivenClientToSubscribe.add(lastListOfSymbolsToSubscribe);
        }

//        List<WebSocket> webSocketList = new ArrayList<>();
        for (int i = 0; i < numberOfClientsToCreate; i++) {
            OkHttpClient client = clientFactory.okHttpClient();

            if (i == 0) {
                Request request = new Request.Builder()
                        .url("wss://ws.finnhub.io?token=col6umhr01qkduilq8r0col6umhr01qkduilq8rg")
                        //token for ggroch95@gmail.com
                        .build();

                WebSocket webSocket = client.newWebSocket(request, handlerFactory.getFinnHubApiHandler(listOfListOfSymbolsForGivenClientToSubscribe.get(i)));
                log.info("Created web socket client {}. Thread: {}", webSocket.toString(), Thread.currentThread().getName());
            }
            if (i == 1) {
                Request request = new Request.Builder()
                        .url("wss://ws.finnhub.io?token=cnli7c9r01qk2u6r38j0cnli7c9r01qk2u6r38jg")
                        //token for piotrgrochowiecki@gmail.com
                        .build();

                WebSocket webSocket = client.newWebSocket(request, handlerFactory.getFinnHubApiHandler(listOfListOfSymbolsForGivenClientToSubscribe.get(i)));
                log.info("Created web socket client {}. Thread: {}", webSocket.toString(), Thread.currentThread().getName());
            }

        }
//        return webSocketList;
    }

    @PostConstruct
    public void init() {
        provideWebSocketClients();
    }
}
