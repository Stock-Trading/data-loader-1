package com.stocktrading.dataloader1.domain.service;

import com.stocktrading.dataloader1.domain.mapper.DataMapper;
import com.stocktrading.dataloader1.domain.model.FinancialInstrumentPriceModel;
import com.stocktrading.dataloader1.domain.processor.DataProcessor;
import com.stocktrading.dataloader1.domain.queue.FinancialInstrumentPriceConsumer;
import com.stocktrading.dataloader1.domain.queue.FinancialInstrumentPriceProducer;
import com.stocktrading.dataloader1.domain.queue.FinancialInstrumentPriceQueue;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FinancialInstrumentPriceService implements DataProcessor {

    private final TaskExecutor dataProcessorTaskExecutor;
    private final DataMapper<FinancialInstrumentPriceModel> mapper;
    private final FinancialInstrumentPriceProducer priceProducer;

    FinancialInstrumentPriceService(TaskExecutor dataProcessorTaskExecutor,
                                    DataMapper<FinancialInstrumentPriceModel> mapper,
                                    FinancialInstrumentPriceProducer priceProducer) {
        this.dataProcessorTaskExecutor = dataProcessorTaskExecutor;
        this.mapper = mapper;
        this.priceProducer = priceProducer;
    }

    /**
     * Streams `FinancialInstrumentPriceModel` data (events) into internal queue.
     *
     * @param data any data that can be deserialized into Financial Instrument Price
     * @see FinancialInstrumentPriceModel
     * @see FinancialInstrumentPriceQueue
     * @see FinancialInstrumentPriceConsumer
     */
    @Override
    public void process(String data) {
        dataProcessorTaskExecutor.execute(() -> {
                    List<FinancialInstrumentPriceModel> priceList = mapper.map(data);
                    priceList.forEach(priceProducer::produce);
                }
        );
    }

}
