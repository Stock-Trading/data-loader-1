package com.stocktrading.dataloader1.remote.finnHub;

import tools.jackson.databind.ObjectMapper;
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
        log.debug("FinnHub WS client: received message: {}", text);
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
                FinancialInstrumentPriceReceivedEvent financialInstrumentPriceReceivedEvent =
                        new FinancialInstrumentPriceReceivedEvent(FinnHubApiHandler.class,
                                financialInstrumentPriceModelList);
                eventPublisher.publishEvent(financialInstrumentPriceReceivedEvent);
            }
        } catch (Exception e) {
            log.error("FinnHub WS client: caught {} on message: {}", e.getClass(), e.getMessage());
        }
    }

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        List<String> listOfInstrumentSymbols = getListOfInstrumentSymbolsAsJsonsToSubscribeOnStartup();
        listOfInstrumentSymbols.forEach(request -> {
            webSocket.send(request);
            log.info("FinnHub WS client: sent message on connection opening: {}", request);
        });
    }

    @Override
    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        log.info("FinnHub WS client: server closed connection to {} code: {}, reason {}", webSocket, code, reason);
    }

    @Override
    public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
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
