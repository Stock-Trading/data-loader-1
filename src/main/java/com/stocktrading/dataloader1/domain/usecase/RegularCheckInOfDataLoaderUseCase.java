package com.stocktrading.dataloader1.domain.usecase;

import com.stocktrading.dataloader1.domain.service.DataLoaderService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RegularCheckInOfDataLoaderUseCase {

    private final DataLoaderService dataLoaderService;

    public RegularCheckInOfDataLoaderUseCase(DataLoaderService dataLoaderService) {
        this.dataLoaderService = dataLoaderService;
    }

    @Scheduled(fixedDelay = 1000)
    public void checkInRegularly() {
        dataLoaderService.checkInDataLoaderToSubscriptionManager();
    }

}
