package com.stocktrading.dataloader1.data;

import com.stocktrading.dataloader1.domain.FinancialInstrumentModel;
import org.springframework.stereotype.Component;

@Component
class DataMapper {

    FinancialInstrumentModel mapToModel(FinancialInstrumentEntity entity) {
        return FinancialInstrumentModel.builder()
                .id(entity.getId())
                .name(entity.getName())
                .symbol(entity.getSymbol())
                .build();
    }

    FinancialInstrumentEntity mapToEntity(FinancialInstrumentModel model) {
        return FinancialInstrumentEntity.builder()
                .name(model.name())
                .symbol(model.symbol())
                .build();
    }
}
