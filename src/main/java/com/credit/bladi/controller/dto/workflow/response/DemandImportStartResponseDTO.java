package com.credit.bladi.controller.dto.workflow.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "DemandImportStartResponseDTO", description = "Identifier of a just-launched demand "
    + "import job, to be polled via GET /api/v1/demands/import/{jobExecutionId}.")
public record DemandImportStartResponseDTO(
    @Schema(description = "Spring Batch job execution id.", example = "42") Long jobExecutionId
) {}
