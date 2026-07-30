package com.stocktrading.dataloader1.remote.subbscriptionmanager;

import lombok.Builder;

import java.util.List;

@Builder
record SubscriptionDto(String dataLoaderUuid,
                       List<FinancialInstrumentResponseDto> financialInstrumentResponseDtoList) {
}
