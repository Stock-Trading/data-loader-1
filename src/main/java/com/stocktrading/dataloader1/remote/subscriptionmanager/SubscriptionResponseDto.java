package com.stocktrading.dataloader1.remote.subscriptionmanager;

import lombok.Builder;

import java.util.List;

@Builder
record SubscriptionResponseDto(String dataLoaderUuid,
                               List<FinancialInstrumentResponseDto> financialInstrumentResponseDtoList) {
}
