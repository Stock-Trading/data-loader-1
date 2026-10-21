package com.stocktrading.dataloader1.domain.service;

import com.stocktrading.dataloader1.domain.model.SubscriptionModel;
import com.stocktrading.dataloader1.domain.ports.SubscriptionManagerClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class DataLoaderService {

    private final SubscriptionManagerClient subscriptionManagerClient;
    private final AtomicBoolean registeredToSubscriptionManager = new AtomicBoolean(false);

    public DataLoaderService(SubscriptionManagerClient subscriptionManagerClient) {
        this.subscriptionManagerClient = subscriptionManagerClient;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Async
    public void registerDataLoaderToSubscriptionManagerOnStartup() {
        log.info("Starting procedure of registering Data Loader to Subscription Manager after application startup");
        tryToRegisterToSubscriptionManager();
    }

    /// Retries registration periodically in case Subscription Manager was offline on startup
    /// (or became unreachable afterward), so a temporary outage of that service never prevents
    /// this application from starting or keeps running.
    @Scheduled(fixedDelay = 30000, initialDelay = 30000)
    @Async
    public void retryRegistrationToSubscriptionManagerIfNeeded() {
        if (!registeredToSubscriptionManager.get()) {
            log.info("Data Loader is not yet registered in Subscription Manager, retrying registration");
            tryToRegisterToSubscriptionManager();
        }
    }

    private void tryToRegisterToSubscriptionManager() {
        try {
            subscriptionManagerClient.registerToSubscriptionManager();
            registeredToSubscriptionManager.set(true);
        } catch (Exception ex) {
            log.warn("Could not register Data Loader in Subscription Manager, it may be offline. " +
                    "Will retry periodically. Reason: {}", ex.getMessage());
        }
    }

    public void checkInDataLoaderToSubscriptionManager() {
        log.debug("Starting procedure of checking in Data Loader to Subscription Manager");
        try {
            subscriptionManagerClient.checkInToSubscriptionManager();
        } catch (Exception ex) {
            log.warn("Could not check in Data Loader to Subscription Manager, it may be offline. Reason: {}", ex.getMessage());
        }
    }
}
