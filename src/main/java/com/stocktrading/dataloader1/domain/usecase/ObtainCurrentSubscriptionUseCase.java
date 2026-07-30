package com.stocktrading.dataloader1.domain.usecase;

import com.stocktrading.dataloader1.domain.model.SubscriptionModel;
import com.stocktrading.dataloader1.domain.ports.SubscriptionManagerClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ObtainCurrentSubscriptionUseCase {

    private final SubscriptionManagerClient subscriptionManagerClient;

    public ObtainCurrentSubscriptionUseCase(SubscriptionManagerClient subscriptionManagerClient) {
        this.subscriptionManagerClient = subscriptionManagerClient;
    }

    @Scheduled(fixedDelay = 5000)
    public void obtainSubscription() {
        SubscriptionModel model = subscriptionManagerClient.getSubscription();
        log.info("Current subscription: {}", model.toString());
    }
}
