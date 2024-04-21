package com.stocktrading.dataloader1.domain;

import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@Getter
@ToString
public class InstrumentSubscriptionStateChangedEvent extends ApplicationEvent {

    private final InstrumentSubscriptionStateChangeType action;
    private final FinancialInstrumentModel financialInstrument;

    public InstrumentSubscriptionStateChangedEvent(Object source, InstrumentSubscriptionStateChangeType action, FinancialInstrumentModel financialInstrument) {
        super(source);
        this.action = action;
        this.financialInstrument = financialInstrument;
    }

}
