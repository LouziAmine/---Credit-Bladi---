package com.credit.bladi.datasource.importdemand.mapper;

import com.credit.bladi.datasource.importdemand.entity.DemandImportRowErrorEntity;
import com.credit.bladi.model.importdemand.DemandImportRowErrorModel;

public final class DemandImportRowErrorEntityMapper {
    private DemandImportRowErrorEntityMapper() {
    }

    public static DemandImportRowErrorModel toModel(DemandImportRowErrorEntity entity) {
        return new DemandImportRowErrorModel(entity.getId(), entity.getJobExecutionId(), entity.getRowNumber(),
            entity.getCompanyName(), entity.getErrorMessage(), entity.getCreatedAt());
    }
}
