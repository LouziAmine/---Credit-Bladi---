package com.credit.bladi.controller.dto.workflow.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "DemandImportRowErrorResponseDTO", description = "One skipped Excel line.")
public record DemandImportRowErrorResponseDTO(
    @Schema(description = "1-based row number as seen in the spreadsheet (header included).", example = "7")
    Integer rowNumber,
    @Schema(description = "companyName cell of the failed row, when it could be read (null if the row "
        + "itself failed to parse at all).", example = "Atlas Trading") String companyName,
    @Schema(description = "Why this row was skipped.", example = "amount is required and must be numeric")
    String errorMessage
) {}
