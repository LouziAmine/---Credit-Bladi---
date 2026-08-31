package com.credit.bladi.batch.demand;

import com.credit.bladi.batch.excel.ImportRowException;
import com.credit.bladi.exception.WorkflowException;
import com.credit.bladi.service.workflow.engine.IWorkflowEngineService;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;

public class DemandImportItemWriter implements ItemWriter<DemandImportItem> {
    private final IWorkflowEngineService workflowEngineService;

    public DemandImportItemWriter(IWorkflowEngineService workflowEngineService) {
        this.workflowEngineService = workflowEngineService;
    }

    @Override
    public void write(Chunk<? extends DemandImportItem> chunk) {
        for (DemandImportItem item : chunk) {
            try {
                workflowEngineService.createDemand(item.snapshot());
            } catch (WorkflowException e) {
                throw new ImportRowException(item.rowNumber(), e.getMessage());
            }
        }
    }
}
