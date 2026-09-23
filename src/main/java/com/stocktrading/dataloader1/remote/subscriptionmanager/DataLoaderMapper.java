package com.stocktrading.dataloader1.remote.subscriptionmanager;

import com.stocktrading.dataloader1.domain.model.DataLoaderModel;
import com.stocktrading.dataloader1.domain.model.FinancialInstrumentModel;
import com.stocktrading.dataloader1.domain.model.SubscriptionModel;
import org.springframework.stereotype.Component;

@Component
class DataLoaderMapper {

    DataLoaderDto mapToDto(DataLoaderModel model){
        return DataLoaderDto.builder()
                .uuid(model.getUuid())
                .build();
    }

    FinancialInstrumentModel mapToModel(FinancialInstrumentResponseDto dto) {
        return FinancialInstrumentModel.builder()
                .name(dto.name())
                .symbol(dto.symbol())
                .build();
    }

    SubscriptionModel mapToModel(SubscriptionResponseDto dto) {
        return SubscriptionModel.builder()
                .financialInstrumentModelList(dto.financialInstrumentResponseDtoList()
                        .stream()
                        .map(this::mapToModel)
                        .toList())
                .build();
    }
}
