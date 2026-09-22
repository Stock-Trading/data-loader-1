package com.stocktrading.dataloader1.domain.usecase;

import com.stocktrading.dataloader1.domain.model.FinancialInstrumentModel;
import com.stocktrading.dataloader1.domain.model.SubscriptionModel;
import com.stocktrading.dataloader1.domain.ports.FinancialInstrumentRepository;
import com.stocktrading.dataloader1.domain.ports.SubscriptionManagerClient;
import com.stocktrading.dataloader1.domain.service.FinancialInstrumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
public class ObtainCurrentSubscriptionUseCase {

    private final SubscriptionManagerClient subscriptionManagerClient;
    private final FinancialInstrumentRepository financialInstrumentRepository;
    private final FinancialInstrumentService financialInstrumentService;

    public ObtainCurrentSubscriptionUseCase(SubscriptionManagerClient subscriptionManagerClient,
                                            FinancialInstrumentRepository financialInstrumentRepository,
                                            FinancialInstrumentService financialInstrumentService) {
        this.subscriptionManagerClient = subscriptionManagerClient;
        this.financialInstrumentRepository = financialInstrumentRepository;
        this.financialInstrumentService = financialInstrumentService;
    }

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void obtainSubscription() {
        SubscriptionModel model = subscriptionManagerClient.getSubscription();
        log.info("Current subscription: {}", model.toString());
        checkForChange(model);
    }

    /// compares latest subscription (not yet subscribed) with the current one
    private void checkForChange(SubscriptionModel latestSubscription) {
        List<FinancialInstrumentModel> oldFIs = financialInstrumentRepository.findAll();
        List<FinancialInstrumentModel> latestFIs = latestSubscription.financialInstrumentModelList();

        latestFIs.stream()
                .filter(fi -> !oldFIs.contains(fi))
                .forEach(financialInstrumentService::subscribeToTheInstrument);

        oldFIs.stream()
                .filter(fi -> !latestFIs.contains(fi))
                .forEach(fi -> financialInstrumentService.unsubscribeFromTheInstrumentBySymbol(
                        fi.symbol()));
    }

}
