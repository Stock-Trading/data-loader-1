package com.stocktrading.dataloader1.domain;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Log4j2
public class FinancialInstrumentService {

    private final FinancialInstrumentRepository repository;

    public FinancialInstrumentModel getById(Long id) {
        return repository.findById(id);
    }

    public FinancialInstrumentModel getByName(String name) {
        return repository.findByName(name);
    }

    public FinancialInstrumentModel getBySymbol(String symbol) {
        return repository.findBySymbol(symbol);
    }

    public List<FinancialInstrumentModel> getAllCurrentlySubscribed() {
        return repository.findAll();
    }

    public FinancialInstrumentModel subscribeToTheInstrument(FinancialInstrumentModel model) {
        return repository.save(model);
    }

    @Transactional
    public FinancialInstrumentModel unsubscribeFromTheInstrumentById(Long id) {
        if (existsById(id)) {
            FinancialInstrumentModel instrumentToBeUnsubscribed = repository.findById(id);
            repository.deleteById(id);
            log.info("Unsubscribed from instrument with id of {}", id);
            return instrumentToBeUnsubscribed;
        } else {
            log.info("Tried to unsubscribe from instrument with id of {}, but no entity with given id was found", id);
            return null;
        }
    }

    @Transactional
    public FinancialInstrumentModel unsubscribeFromTheInstrumentByName(String name) {
        if (existsByName(name)) {
            FinancialInstrumentModel instrumentToBeUnsubscribed = repository.findByName(name);
            repository.deleteByName(name);
            log.info("Unsubscribed from instrument with name of {}", name);
            return instrumentToBeUnsubscribed;
        } else {
            log.info("Tried to unsubscribe from instrument with name of {}, but no entity with given id was found", name);
            return null;
        }
    }

    @Transactional
    public FinancialInstrumentModel unsubscribeFromTheInstrumentBySymbol(String symbol) {
        if (existsBySymbol(symbol)) {
            FinancialInstrumentModel instrumentToBeUnsubscribed = repository.findBySymbol(symbol);
            repository.deleteBySymbol(symbol);
            log.info("Unsubscribed from instrument with symbol of {}", symbol);
            return instrumentToBeUnsubscribed;
        } else {
            log.info("Tried to unsubscribe from instrument with symbol of {}, but no entity with given symbol was found", symbol);
            return null;
        }
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
