package com.stocktrading.dataloader1.domain;

import com.stocktrading.dataloader1.domain.model.FinancialInstrumentPriceModel;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class StockPriceReceivedEvent extends ApplicationEvent {

    private final List<FinancialInstrumentPriceModel> financialInstrumentPriceModelList;

    public StockPriceReceivedEvent(Object source, List<FinancialInstrumentPriceModel> financialInstrumentPriceModelList) {
        super(source);
        this.financialInstrumentPriceModelList = financialInstrumentPriceModelList;
    }

}
