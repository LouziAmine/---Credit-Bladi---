package com.credit.bladi.repository.servicing;

import com.credit.bladi.datasource.servicing.CreditFileDatasource;
import com.credit.bladi.datasource.servicing.entity.CreditFileEntity;
import com.credit.bladi.datasource.servicing.mapper.CreditFileEntityMapper;
import com.credit.bladi.model.servicing.CreditFileModel;
import com.credit.bladi.model.vo.Money;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class CreditFileRepository implements ICreditFileRepository {
    private final CreditFileDatasource datasource;

    public CreditFileRepository(CreditFileDatasource datasource) {
        this.datasource = datasource;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public CreditFileModel create(Long demandId, String reference, Instant disbursedAt, Money disbursedAmount,
            String managedByUsername) {
        CreditFileEntity entity = CreditFileEntityMapper.mapToNewEntity(demandId, reference, disbursedAt,
            disbursedAmount, managedByUsername);
        datasource.save(entity);
        return CreditFileEntityMapper.mapFromEntity(entity);
    }

    @Override
    public Optional<CreditFileModel> findById(Long id) {
        return datasource.findById(id).map(CreditFileEntityMapper::mapFromEntity);
    }

    @Override
    public List<CreditFileModel> findAllById(List<Long> ids) {
        return datasource.findAllById(ids).stream().map(CreditFileEntityMapper::mapFromEntity).toList();
    }

    @Override
    public Optional<CreditFileModel> findByDemandId(Long demandId) {
        return datasource.findByDemandId(demandId).map(CreditFileEntityMapper::mapFromEntity);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Optional<CreditFileModel> findByDemandIdForUpdate(Long demandId) {
        return datasource.findByDemandIdForUpdate(demandId).map(CreditFileEntityMapper::mapFromEntity);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean closeIfActive(Long id) {
        return datasource.closeIfActive(id) > 0;
    }
}
