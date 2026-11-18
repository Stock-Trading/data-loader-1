package com.stocktrading.dataloader1.domain.queue;

import com.stocktrading.dataloader1.domain.model.FinancialInstrumentPriceModel;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Queue used to store price events {@link FinancialInstrumentPriceModel} in FIFO order.
 * It allows for asynchronous processing of various steps.
 *
 * @see FinancialInstrumentPriceModel
 */
@Component
public class FinancialInstrumentPriceQueue {

    private final BlockingQueue<FinancialInstrumentPriceModel> queue;

    public FinancialInstrumentPriceQueue() {
        this.queue = new LinkedBlockingQueue<>();
    }

    public void put(FinancialInstrumentPriceModel priceModel) throws InterruptedException {
        queue.put(priceModel);
    }

    public FinancialInstrumentPriceModel take() throws InterruptedException {
        return queue.take();
    }

}
