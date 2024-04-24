package com.stocktrading.dataloader1.domain.event;

import com.stocktrading.dataloader1.domain.model.FinancialInstrumentModel;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@Getter
@ToString
public class FinancialInstrumentSubscriptionStateChangedEvent extends ApplicationEvent {

    private final FinancialInstrumentSubscriptionStateChangeType action;
    private final FinancialInstrumentModel financialInstrument;

    public FinancialInstrumentSubscriptionStateChangedEvent(Object source, FinancialInstrumentSubscriptionStateChangeType changeType, FinancialInstrumentModel financialInstrument) {
        super(source);
        this.action = changeType;
        this.financialInstrument = financialInstrument;
    }

}
