package com.stocktrading.dataloader1.remote.finnHub;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
class FinnHubApiHandlerFactory {

    private final FinancialInstrumentPriceMapper mapper;
    private final ApplicationEventPublisher eventPublisher;

    FinnHubApiHandler getFinnHubApiHandler(List<String> listOfInstrumentsToSubscribeOnStartup) {
        return new FinnHubApiHandler(mapper, eventPublisher, listOfInstrumentsToSubscribeOnStartup);
    }
}
