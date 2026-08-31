package com.credit.bladi.service.importdemand;

import com.credit.bladi.model.importdemand.DemandImportReport;
import com.credit.bladi.model.workflow.UploadedFile;

public interface IDemandImportService {
    Long startImport(UploadedFile file);

    DemandImportReport getReport(Long jobExecutionId);

    void recordRowError(Long jobExecutionId, int rowNumber, String companyName, String errorMessage);
}
