package com.credit.bladi.repository.importdemand;

import com.credit.bladi.datasource.importdemand.DemandImportRowErrorDatasource;
import com.credit.bladi.datasource.importdemand.entity.DemandImportRowErrorEntity;
import com.credit.bladi.datasource.importdemand.mapper.DemandImportRowErrorEntityMapper;
import com.credit.bladi.model.importdemand.DemandImportRowErrorModel;
import java.time.Clock;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class DemandImportRepository implements IDemandImportRepository {
    private final DemandImportRowErrorDatasource datasource;

    private final Clock clock;

    public DemandImportRepository(DemandImportRowErrorDatasource datasource, Clock clock) {
        this.clock = clock;
        this.datasource = datasource;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordRowError(Long jobExecutionId, int rowNumber, String companyName, String errorMessage) {
        DemandImportRowErrorEntity entity = new DemandImportRowErrorEntity();
        entity.setJobExecutionId(jobExecutionId);
        entity.setRowNumber(rowNumber);
        entity.setCompanyName(companyName);
        entity.setErrorMessage(errorMessage.length() > 500 ? errorMessage.substring(0, 500) : errorMessage);
        entity.setCreatedAt(clock.instant());
        datasource.save(entity);
    }

    @Override
    public List<DemandImportRowErrorModel> findRowErrors(Long jobExecutionId) {
        return datasource.findByJobExecutionIdOrderByRowNumberAsc(jobExecutionId).stream()
            .map(DemandImportRowErrorEntityMapper::toModel)
            .toList();
    }
}
