package com.stocktrading.dataloader1.domain.ports;

import com.stocktrading.dataloader1.domain.model.SubscriptionModel;

public interface SubscriptionManagerClient {

    void registerToSubscriptionManager();

    void checkInToSubscriptionManager();

    SubscriptionModel getSubscription();

}
