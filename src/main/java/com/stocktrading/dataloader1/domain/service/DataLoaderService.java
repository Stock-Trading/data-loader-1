package com.stocktrading.dataloader1.domain.service;

import com.stocktrading.dataloader1.domain.model.SubscriptionModel;
import com.stocktrading.dataloader1.domain.ports.SubscriptionManagerClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataLoaderService {

    private final SubscriptionManagerClient subscriptionManagerClient;

    public DataLoaderService(SubscriptionManagerClient subscriptionManagerClient) {
        this.subscriptionManagerClient = subscriptionManagerClient;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void registerDataLoaderToSubscriptionManagerOnStartup() {
        log.info("Starting procedure of registering Data Loader to Subscription Manager after application startup");
        subscriptionManagerClient.registerToSubscriptionManager();
    }

    public void checkInDataLoaderToSubscriptionManager() {
        log.debug("Starting procedure of checking in Data Loader to Subscription Manager");
        subscriptionManagerClient.checkInToSubscriptionManager();
    }
}
