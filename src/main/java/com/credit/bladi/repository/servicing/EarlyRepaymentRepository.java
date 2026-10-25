package com.credit.bladi.repository.servicing;

import com.credit.bladi.datasource.servicing.EarlyRepaymentDatasource;
import com.credit.bladi.datasource.servicing.entity.EarlyRepaymentEntity;
import com.credit.bladi.datasource.servicing.mapper.EarlyRepaymentEntityMapper;
import com.credit.bladi.model.servicing.EarlyRepaymentModel;
import com.credit.bladi.model.servicing.EarlyRepaymentQuote;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class EarlyRepaymentRepository implements IEarlyRepaymentRepository {
    private final EarlyRepaymentDatasource datasource;

    public EarlyRepaymentRepository(EarlyRepaymentDatasource datasource) {
        this.datasource = datasource;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public EarlyRepaymentModel create(Long creditFileId, EarlyRepaymentQuote quote, String performedBy,
            Instant performedAt) {
        EarlyRepaymentEntity entity = EarlyRepaymentEntityMapper.mapToNewEntity(creditFileId, quote, performedBy,
            performedAt);
        datasource.save(entity);
        return EarlyRepaymentEntityMapper.mapFromEntity(entity);
    }

    @Override
    public List<EarlyRepaymentModel> findByCreditFileId(Long creditFileId) {
        return datasource.findByCreditFileIdOrderByPerformedAtAsc(creditFileId).stream()
            .map(EarlyRepaymentEntityMapper::mapFromEntity)
            .toList();
    }
}
