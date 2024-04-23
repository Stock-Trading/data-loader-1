package com.stocktrading.dataloader1.remote.finnHub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stocktrading.dataloader1.domain.*;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
@AllArgsConstructor
public class FinnHubApiHandler extends WebSocketListener {

    private final StockPriceMapper mapper;
    private final StockPriceService service;
    private final FinancialInstrumentService financialInstrumentService;

    private final static ObjectMapper jsonMapper = new ObjectMapper();

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        log.info("Received message from server: {}", text);
        try {
            if (text.equals("{\"type\":\"ping\"}")) {
                String pong = "{\"type\":\"pong\"}";
                webSocket.send(pong);
                log.info("Sent \"pong\" message");
            } else {
                FinnHubTradeResponseDto finnHubTradeResponseDto = jsonMapper.readValue(text, FinnHubTradeResponseDto.class);
                List<StockPriceModel> stockPriceModelList = finnHubTradeResponseDto.dataList()
                        .stream()
                        .map(mapper::mapToModel)
                        .toList();
                StockPriceReceivedEvent stockPriceReceivedEvent = new StockPriceReceivedEvent(FinnHubApiHandler.class, stockPriceModelList);
                service.publishStockPriceReceivedAsAppEvent(stockPriceReceivedEvent);
            }
        } catch (Exception e) {
            log.error("Caught exception: {}", e.getMessage());
        }
    }

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        List<String> listOfInstrumentSymbols = getListOfInstrumentSymbolsAsJsonsToSubscribeOnStartup();
        listOfInstrumentSymbols.forEach(request -> {
            webSocket.send(request);
            log.info("Sent message on FinnHub connection opening: {}", request);
        });
    }

    @Override
    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        log.info("Closed connection to: {} code: {}", webSocket, code);
    }

    @Override
    public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        log.info("Closing connection to: {} code: {}", webSocket, code);
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
        log.error("Failure connecting to {}. Response:{}. Throwable: {}", webSocket, response, t);
    }

    private List<String> getListOfInstrumentSymbolsAsJsonsToSubscribeOnStartup() {
        List<String> listOfSymbols = financialInstrumentService.getAllSymbolsOfCurrentlySubscribed();
        return listOfSymbols.stream()
                .map(symbol -> FinnHubMessageRequestDto.builder()
                        .type(FinnHubMessageType.SUBSCRIBE.getMessageType())
                        .symbol(symbol)
                        .build())
                .map(requestDto -> {
                    try {
                        return jsonMapper.writeValueAsString(requestDto);
                    } catch (JsonProcessingException jpe) {
                        throw new FinnHubApiClientRuntimeException("Exception while serializing to json: " + jpe.getMessage());
                    }
                })
                .toList();
    }
}