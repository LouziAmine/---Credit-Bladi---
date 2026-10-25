package com.credit.bladi.controller.workflow.mapper;

import com.credit.bladi.controller.dto.workflow.response.DemandImportReportResponseDTO;
import com.credit.bladi.controller.dto.workflow.response.DemandImportRowErrorResponseDTO;
import com.credit.bladi.controller.dto.workflow.response.DemandImportStartResponseDTO;
import com.credit.bladi.model.importdemand.DemandImportReport;
import com.credit.bladi.model.importdemand.DemandImportRowErrorModel;

public final class DemandImportMapper {
    private DemandImportMapper() {
    }

    public static DemandImportStartResponseDTO toStartResponseDTO(Long jobExecutionId) {
        return new DemandImportStartResponseDTO(jobExecutionId);
    }

    public static DemandImportReportResponseDTO toResponseDTO(DemandImportReport report) {
        return new DemandImportReportResponseDTO(
            report.jobExecutionId(),
            report.status(),
            report.readCount(),
            report.writeCount(),
            report.skipCount(),
            report.startTime(),
            report.endTime(),
            report.rowErrors().stream().map(DemandImportMapper::toResponseDTO).toList());
    }

    private static DemandImportRowErrorResponseDTO toResponseDTO(DemandImportRowErrorModel model) {
        return new DemandImportRowErrorResponseDTO(
            model.rowNumber(),
            model.companyName(),
            model.errorMessage());
    }
}
