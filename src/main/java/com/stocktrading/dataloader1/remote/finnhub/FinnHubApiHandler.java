package com.stocktrading.dataloader1.remote.finnhub;

import com.stocktrading.dataloader1.domain.model.FinancialInstrumentPriceModel;
import com.stocktrading.dataloader1.domain.queue.FinancialInstrumentPriceProducer;
import com.stocktrading.dataloader1.domain.service.FinancialInstrumentService;
import lombok.extern.log4j.Log4j2;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Component
@Log4j2
class FinnHubApiHandler extends WebSocketListener {

    private final FinnHubMapper mapper;
    private final FinancialInstrumentService financialInstrumentService;
    private final FinancialInstrumentPriceProducer priceProducer;
    private final ObjectMapper jsonMapper;
    private final TaskExecutor finnHubMapperExecutor;

    private boolean running = false;

    FinnHubApiHandler(FinnHubMapper mapper,
                      FinancialInstrumentService financialInstrumentService,
                      FinancialInstrumentPriceProducer priceProducer,
                      ObjectMapper jsonMapper,
                      TaskExecutor finnHubMapperExecutor) {
        this.mapper = mapper;
        this.financialInstrumentService = financialInstrumentService;
        this.priceProducer = priceProducer;
        this.jsonMapper = jsonMapper;
        this.finnHubMapperExecutor = finnHubMapperExecutor;
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
                finnHubMapperExecutor.execute(() -> {
                    log.info("Deserializing FinnHub response {}...", finnHubResponseJson.substring(1, 15));
                            List<FinancialInstrumentPriceModel> priceList = mapper.deserialize(finnHubResponseJson);
                            priceList.forEach(it -> {
                                try {
                                    priceProducer.produce(it);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        }
                );
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
