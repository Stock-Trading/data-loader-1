package com.stocktrading.dataloader1.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FinancialInstrumentSubscriptionStateChangeType {

    SUBSCRIBED("subscribed"),
    UNSUBSCRIBED("unsubscribed");

    private final String subscriptionAction;
}
