package com.credit.bladi.controller.workflow;

import com.credit.bladi.controller.dto.workflow.response.DemandImportReportResponseDTO;
import com.credit.bladi.controller.dto.workflow.response.DemandImportStartResponseDTO;
import com.credit.bladi.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Demand Import", description = "Generic, reusable Spring Batch pipeline (see batch/) to bulk-"
    + "create demands from an Excel file of pre-existing dossiers, each going through the exact same "
    + "workflow initialization as a demand created one at a time via the API.")
public interface IDemandImportController {
    @Operation(
        summary = "Upload an Excel file of demands to import",
        description = "One column per DemandSnapshot field, in a fixed order (see docs). Returns "
            + "immediately with a jobExecutionId: the import itself runs asynchronously; poll "
            + "GET /{jobExecutionId} for progress. Only one import may run at a time."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "202", description = "Import started",
            content = @Content(schema = @Schema(implementation = DemandImportStartResponseDTO.class))),
        @ApiResponse(responseCode = "409", description = "An import is already running",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "422", description = "File is empty, exceeds the maximum size, or is "
            + "not a .xlsx spreadsheet",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DemandImportStartResponseDTO> startImport(
        @Parameter(description = "The .xlsx file to import.")
        @RequestParam("file") MultipartFile file
    );

    @Operation(
        summary = "Get a demand import job's status and per-row outcome",
        description = "Read/write/skip counts come from Spring Batch itself; rowErrors lists exactly "
            + "which lines were skipped and why, so the uploader can fix and re-import just those rows."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Import status",
            content = @Content(schema = @Schema(implementation = DemandImportReportResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "No import found for the given job execution ID",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DemandImportReportResponseDTO> getReport(
        @Parameter(description = "Spring Batch job execution id, from the startImport response.", example = "42")
        @Min(value = 1, message = "{validation.id.positive}")
        @PathVariable Long jobExecutionId
    );
}
