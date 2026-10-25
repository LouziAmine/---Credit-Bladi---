package com.credit.bladi.batch.demand;

import com.credit.bladi.batch.excel.ImportRowException;
import com.credit.bladi.service.importdemand.IDemandImportService;
import org.springframework.batch.core.listener.SkipListener;

public class DemandImportSkipListener implements SkipListener<DemandExcelRow, DemandImportItem> {
    private final IDemandImportService demandImportService;

    private final Long jobExecutionId;

    public DemandImportSkipListener(IDemandImportService demandImportService, Long jobExecutionId) {
        this.demandImportService = demandImportService;
        this.jobExecutionId = jobExecutionId;
    }

    @Override
    public void onSkipInRead(Throwable throwable) {
        ImportRowException importRowException = unwrap(throwable);
        int rowNumber = importRowException != null ? importRowException.getRowNumber() : -1;
        demandImportService.recordRowError(jobExecutionId, rowNumber, null, message(importRowException, throwable));
    }

    @Override
    public void onSkipInProcess(DemandExcelRow item, Throwable throwable) {
        demandImportService.recordRowError(jobExecutionId, item.rowNumber(), item.companyName(),
            message(unwrap(throwable), throwable));
    }

    @Override
    public void onSkipInWrite(DemandImportItem item, Throwable throwable) {
        demandImportService.recordRowError(jobExecutionId, item.rowNumber(), item.companyName(),
            message(unwrap(throwable), throwable));
    }

    private static ImportRowException unwrap(Throwable throwable) {
        while (throwable != null) {
            if (throwable instanceof ImportRowException importRowException) {
                return importRowException;
            }
            throwable = throwable.getCause();
        }
        return null;
    }

    private static String message(ImportRowException unwrapped, Throwable original) {
        return unwrapped != null ? unwrapped.getMessage() : original.getMessage();
    }
}
