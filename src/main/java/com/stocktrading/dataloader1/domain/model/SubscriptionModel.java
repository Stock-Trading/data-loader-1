package com.stocktrading.dataloader1.domain.model;

import lombok.Builder;

import java.util.List;

/// Subscription in a context of this whole service shall be understood as a list of Financial Instruments
/// for which this service should fetch live prices from public API and stream them into the system
/// @param financialInstrumentModelList
@Builder
public record SubscriptionModel(List<FinancialInstrumentModel> financialInstrumentModelList) {

}
