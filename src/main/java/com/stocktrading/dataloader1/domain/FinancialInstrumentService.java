package com.stocktrading.dataloader1.domain;

import com.stocktrading.dataloader1.domain.exception.AlreadySubscribedException;
import com.stocktrading.dataloader1.domain.exception.ModelNotFoundException;
import com.stocktrading.dataloader1.domain.model.FinancialInstrumentModel;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Log4j2
public class FinancialInstrumentService {

    private final FinancialInstrumentRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public FinancialInstrumentModel getById(Long id) {
        return repository.findById(id).orElseThrow(ModelNotFoundException::new);
    }

    public FinancialInstrumentModel getByName(String name) {
        return repository.findByName(name).orElseThrow(ModelNotFoundException::new);
    }

    public FinancialInstrumentModel getBySymbol(String symbol) {
        return repository.findBySymbol(symbol).orElseThrow(ModelNotFoundException::new);
    }

    public List<FinancialInstrumentModel> getAllCurrentlySubscribed() {
        return repository.findAll();
    }

    public List<String> getAllSymbolsOfCurrentlySubscribed() {
        return repository.findAllSymbols();
    }

    public FinancialInstrumentModel subscribeToTheInstrument(FinancialInstrumentModel model) {
        if (existsByName(model.name()) || existsBySymbol(model.symbol())) {
            String message = "Model with provided name or symbol is already subscribed " + model;
            log.error(message);
            throw new AlreadySubscribedException(message);
        }
        FinancialInstrumentModel instrumentToSubscribedTo = repository.save(model);
        InstrumentSubscriptionStateChangedEvent subscribeEvent = new InstrumentSubscriptionStateChangedEvent(this, InstrumentSubscriptionStateChangeType.SUBSCRIBED, instrumentToSubscribedTo);
        eventPublisher.publishEvent(subscribeEvent);
        log.debug("Published event {}", subscribeEvent.toString());
        return instrumentToSubscribedTo;
    }

    @Transactional
    public FinancialInstrumentModel unsubscribeFromTheInstrumentById(Long id) {
        FinancialInstrumentModel instrumentToUnsubscribeFrom = repository.findById(id).orElseThrow(ModelNotFoundException::new);
        repository.deleteById(id);
        InstrumentSubscriptionStateChangedEvent unsubscribeEvent = new InstrumentSubscriptionStateChangedEvent(this, InstrumentSubscriptionStateChangeType.UNSUBSCRIBED, instrumentToUnsubscribeFrom);
        eventPublisher.publishEvent(unsubscribeEvent);
        log.debug("Published event: {}", unsubscribeEvent.toString());
        return instrumentToUnsubscribeFrom;
    }

    @Transactional
    public FinancialInstrumentModel unsubscribeFromTheInstrumentByName(String name) {
        FinancialInstrumentModel instrumentToUnsubscribeFrom = repository.findByName(name).orElseThrow(ModelNotFoundException::new);
        repository.deleteByName(name);
        InstrumentSubscriptionStateChangedEvent unsubscribeEvent = new InstrumentSubscriptionStateChangedEvent(this, InstrumentSubscriptionStateChangeType.UNSUBSCRIBED, instrumentToUnsubscribeFrom);
        eventPublisher.publishEvent(unsubscribeEvent);
        log.debug("Published event: {}", unsubscribeEvent.toString());
        return instrumentToUnsubscribeFrom;
    }

    @Transactional
    public FinancialInstrumentModel unsubscribeFromTheInstrumentBySymbol(String symbol) {
        FinancialInstrumentModel instrumentToUnsubscribeFrom = repository.findBySymbol(symbol).orElseThrow(ModelNotFoundException::new);
        repository.deleteBySymbol(symbol);
        InstrumentSubscriptionStateChangedEvent unsubscribeEvent = new InstrumentSubscriptionStateChangedEvent(this, InstrumentSubscriptionStateChangeType.UNSUBSCRIBED, instrumentToUnsubscribeFrom);
        eventPublisher.publishEvent(unsubscribeEvent);
        log.debug("Published event: {}", unsubscribeEvent.toString());
        return instrumentToUnsubscribeFrom;
    }

    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    public boolean existsByName(String name) {
        return repository.existsByName(name);
    }

    public boolean existsBySymbol(String Symbol) {
        return repository.existsBySymbol(Symbol);
    }
}
