package com.stocktrading.dataloader1.remote.finnhub;

import com.stocktrading.dataloader1.domain.processor.DataProcessor;
import com.stocktrading.dataloader1.domain.service.FinancialInstrumentService;
import lombok.extern.log4j.Log4j2;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Component
@Log4j2
class FinnHubApiHandler extends WebSocketListener {

    private final FinancialInstrumentService financialInstrumentService;
    private final ObjectMapper jsonMapper;
    private final DataProcessor dataProcessor;

    private boolean running = false;

    FinnHubApiHandler(FinancialInstrumentService financialInstrumentService,
                      ObjectMapper jsonMapper,
                      DataProcessor dataProcessor) {
        this.financialInstrumentService = financialInstrumentService;
        this.jsonMapper = jsonMapper;
        this.dataProcessor = dataProcessor;
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String finnHubResponseJson) {
        log.debug("FinnHub WS client: received message: {}", finnHubResponseJson);
        try {
            if (finnHubResponseJson.equals("{\"type\":\"ping\"}")) {
                String pong = "{\"type\":\"pong\"}";
                webSocket.send(pong);
                log.info("Sent \"pong\" message");
            } else if (running) {
                //messages are deserialized on other thread then one running onMessage() method for near-live processing
                //receiving messages must be as fast as possible
                dataProcessor.process(finnHubResponseJson);
            }
        } catch (Exception e) {
            log.error("FinnHub WS client: caught {} on message: {}", e.getClass(), e.getMessage());
        }
    }

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        running = true;
        List<String> listOfInstrumentSymbols = getListOfInstrumentSymbolsAsJsonsToSubscribeOnStartup();
        listOfInstrumentSymbols.forEach(request -> {
            webSocket.send(request);
            log.info("FinnHub WS client: sent message on connection opening: {}", request);
        });
    }

    @Override
    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        this.running = false;
        log.info("FinnHub WS client: server closed connection to {} code: {}, reason {}", webSocket, code, reason);
    }

    @Override
    public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        this.running = false;
        log.info("FinnHub WS client: closing connection to: {} code: {}, reason {}", webSocket, code, reason);
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
        log.error("FinnHub WS client: connection failure. HTTP status: {}",
                response == null ? null : response.code(), t);
    }

    private List<String> getListOfInstrumentSymbolsAsJsonsToSubscribeOnStartup() {
        List<String> listOfSymbols = financialInstrumentService.getAllSymbolsOfCurrentlySubscribed();
        return listOfSymbols.stream()
                .map(symbol -> FinnHubMessageRequest.builder()
                        .type(FinnHubMessageType.SUBSCRIBE.getMessageType())
                        .symbol(symbol)
                        .build())
                .map(requestDto -> {
                    try {
                        return jsonMapper.writeValueAsString(requestDto);
                    } catch (Exception e) {
                        throw new FinnHubApiClientRuntimeException("Exception while serializing to json: " + e.getMessage());
                    }
                })
                .toList();
    }

}
