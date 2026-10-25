package com.credit.bladi.repository.importdemand;

import com.credit.bladi.model.importdemand.DemandImportRowErrorModel;
import java.util.List;

public interface IDemandImportRepository {
    void recordRowError(Long jobExecutionId, int rowNumber, String companyName, String errorMessage);

    List<DemandImportRowErrorModel> findRowErrors(Long jobExecutionId);
}
