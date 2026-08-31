package com.credit.bladi.controller.dto.workflow.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(name = "DemandImportReportResponseDTO", description = "Status and outcome of a demand import job.")
public record DemandImportReportResponseDTO(
    @Schema(description = "Spring Batch job execution id.", example = "42") Long jobExecutionId,
    @Schema(description = "STARTING, STARTED, COMPLETED, FAILED, STOPPING, STOPPED or ABANDONED.",
        example = "COMPLETED") String status,
    @Schema(description = "Excel rows read (data rows, header excluded).", example = "50") long readCount,
    @Schema(description = "Demands successfully created.", example = "47") long writeCount,
    @Schema(description = "Rows skipped: see rowErrors for why.", example = "3") long skipCount,
    @Schema(description = "When the job started.") LocalDateTime startTime,
    @Schema(description = "When the job finished (null while still running).") LocalDateTime endTime,
    @Schema(description = "One entry per skipped row.") List<DemandImportRowErrorResponseDTO> rowErrors
) {}
