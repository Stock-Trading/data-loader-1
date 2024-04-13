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
    private final Mapper mapper;

    @Override
    public FinancialInstrumentModel findById(Long id) {
        FinancialInstrumentEntity entity = jpaRepository.getReferenceById(id);
        return mapper.mapToModel(entity);
    }

    @Override
    public FinancialInstrumentModel save(FinancialInstrumentModel model) {
        FinancialInstrumentEntity entity = mapper.mapToEntity(model);
        FinancialInstrumentEntity savedEntity = jpaRepository.save(entity);
        return mapper.mapToModel(savedEntity);
    }

    @Override
    public List<FinancialInstrumentModel> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(mapper::mapToModel)
                .toList();
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
