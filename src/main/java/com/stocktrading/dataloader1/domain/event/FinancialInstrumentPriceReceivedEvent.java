package com.stocktrading.dataloader1.domain.event;

import com.stocktrading.dataloader1.domain.model.FinancialInstrumentPriceModel;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class FinancialInstrumentPriceReceivedEvent extends ApplicationEvent {

    private final List<FinancialInstrumentPriceModel> financialInstrumentPriceModelList;

    public FinancialInstrumentPriceReceivedEvent(Object source, List<FinancialInstrumentPriceModel> financialInstrumentPriceModelList) {
        super(source);
        this.financialInstrumentPriceModelList = financialInstrumentPriceModelList;
    }

}
