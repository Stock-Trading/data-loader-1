package com.stocktrading.dataloader1.data;

import com.stocktrading.dataloader1.domain.model.FinancialInstrumentModel;
import com.stocktrading.dataloader1.domain.ports.FinancialInstrumentRepository;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class FinancialInstrumentRepositoryImpl implements FinancialInstrumentRepository {

    private final Map<Long, FinancialInstrumentModel> instruments = new LinkedHashMap<>();
    private long nextId = 1;

    @Override
    public synchronized Optional<FinancialInstrumentModel> findById(Long id) {
        return Optional.ofNullable(instruments.get(id));
    }

    @Override
    public synchronized Optional<FinancialInstrumentModel> findByName(String name) {
        return instruments.values().stream()
                .filter(model -> model.name().equals(name))
                .findFirst();
    }

    @Override
    public synchronized Optional<FinancialInstrumentModel> findBySymbol(String symbol) {
        return instruments.values().stream()
                .filter(model -> model.symbol().equals(symbol))
                .findFirst();
    }

    @Override
    public synchronized List<FinancialInstrumentModel> findAll() {
        return List.copyOf(instruments.values());
    }

    @Override
    public synchronized List<String> findAllSymbols() {
        return instruments.values().stream()
                .map(FinancialInstrumentModel::symbol)
                .toList();
    }

    @Override
    public synchronized FinancialInstrumentModel save(FinancialInstrumentModel model) {
        FinancialInstrumentModel toSave = model.id() != null && instruments.containsKey(model.id())
                ? model
                : FinancialInstrumentModel.builder()
                        .id(nextId++)
                        .name(model.name())
                        .symbol(model.symbol())
                        .build();
        instruments.put(toSave.id(), toSave);
        return toSave;
    }

    @Override
    public synchronized void deleteById(Long id) {
        instruments.remove(id);
    }

    @Override
    public synchronized void deleteByName(String name) {
        instruments.values().removeIf(model -> model.name().equals(name));
    }

    @Override
    public synchronized void deleteBySymbol(String symbol) {
        instruments.values().removeIf(model -> model.symbol().equals(symbol));
    }

    @Override
    public synchronized boolean existsById(Long id) {
        return instruments.containsKey(id);
    }

    @Override
    public synchronized boolean existsByName(String name) {
        return findByName(name).isPresent();
    }

    @Override
    public synchronized boolean existsBySymbol(String symbol) {
        return findBySymbol(symbol).isPresent();
    }
}
