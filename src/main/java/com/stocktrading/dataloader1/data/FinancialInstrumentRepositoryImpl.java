package com.stocktrading.dataloader1.data;

import com.stocktrading.dataloader1.domain.FinancialInstrumentModel;
import com.stocktrading.dataloader1.domain.FinancialInstrumentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class FinancialInstrumentRepositoryImpl implements FinancialInstrumentRepository {

    private final FinancialInstrumentJpaRepository jpaRepository;
    private final DataMapper mapper;

    @Override
    public FinancialInstrumentModel findById(Long id) {
        FinancialInstrumentEntity entity = jpaRepository.getReferenceById(id);
        return mapper.mapToModel(entity);
    }

    @Override
    public FinancialInstrumentModel findByName(String name) {
        FinancialInstrumentEntity entity = jpaRepository.getByName(name);
        return mapper.mapToModel(entity);
    }

    @Override
    public FinancialInstrumentModel findBySymbol(String symbol) {
        FinancialInstrumentEntity entity = jpaRepository.getBySymbol(symbol);
        return mapper.mapToModel(entity);
    }

    @Override
    public List<FinancialInstrumentModel> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(mapper::mapToModel)
                .toList();
    }

    @Override
    public List<String> findAllSymbols() {
        return jpaRepository.getAllSymbols();
    }

    @Override
    public FinancialInstrumentModel save(FinancialInstrumentModel model) {
        FinancialInstrumentEntity entity = mapper.mapToEntity(model);
        FinancialInstrumentEntity savedEntity = jpaRepository.save(entity);
        return mapper.mapToModel(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void deleteByName(String name) {
        jpaRepository.deleteByName(name);
    }

    @Override
    public void deleteBySymbol(String symbol) {
        jpaRepository.deleteBySymbol(symbol);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }

    @Override
    public boolean existsBySymbol(String symbol) {
        return jpaRepository.existsBySymbol(symbol);
    }
}
