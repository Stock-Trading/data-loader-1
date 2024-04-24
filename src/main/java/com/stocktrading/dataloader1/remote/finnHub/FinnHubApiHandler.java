package com.stocktrading.dataloader1.remote.finnHub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stocktrading.dataloader1.domain.event.FinancialInstrumentPriceReceivedEvent;
import com.stocktrading.dataloader1.domain.model.FinancialInstrumentPriceModel;
import com.stocktrading.dataloader1.domain.service.FinancialInstrumentService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
@AllArgsConstructor
public class FinnHubApiHandler extends WebSocketListener {

    private final FinancialInstrumentPriceMapper mapper;
    private final FinancialInstrumentService financialInstrumentService;
    private final ApplicationEventPublisher eventPublisher;

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
                List<FinancialInstrumentPriceModel> financialInstrumentPriceModelList = finnHubTradeResponseDto.dataList()
                        .stream()
                        .map(mapper::mapToModel)
                        .toList();
                FinancialInstrumentPriceReceivedEvent financialInstrumentPriceReceivedEvent = new FinancialInstrumentPriceReceivedEvent(FinnHubApiHandler.class, financialInstrumentPriceModelList);
                eventPublisher.publishEvent(financialInstrumentPriceReceivedEvent);
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
