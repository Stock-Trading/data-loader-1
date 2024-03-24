package com.stocktrading.dataloader1.domain;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class StockPriceReceivedEvent extends ApplicationEvent {

    private final List<StockPriceModel> stockPriceModelList;

    public StockPriceReceivedEvent(Object source, List<StockPriceModel> stockPriceModelList) {
        super(source);
        this.stockPriceModelList = stockPriceModelList;
    }

}
