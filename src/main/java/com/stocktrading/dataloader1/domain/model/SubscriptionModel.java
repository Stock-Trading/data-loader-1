package com.stocktrading.dataloader1.domain.model;

import lombok.Builder;

import java.util.List;

@Builder
public record SubscriptionModel(
        List<FinancialInstrumentModel> financialInstrumentModelList) {

}
