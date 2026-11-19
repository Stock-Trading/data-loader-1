package com.stocktrading.dataloader1.domain.service;

import com.stocktrading.dataloader1.domain.model.FinancialInstrumentPriceModel;
import com.stocktrading.dataloader1.domain.ports.DataMapper;
import com.stocktrading.dataloader1.domain.ports.DataProcessor;
import com.stocktrading.dataloader1.domain.queue.FinancialInstrumentPriceProducer;
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

    @Override
    public void process(String data) {
        dataProcessorTaskExecutor.execute(() -> {
                    List<FinancialInstrumentPriceModel> priceList = mapper.map(data);
                    priceList.forEach(it -> {
                        try {
                            priceProducer.produce(it);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
        );
    }

}
