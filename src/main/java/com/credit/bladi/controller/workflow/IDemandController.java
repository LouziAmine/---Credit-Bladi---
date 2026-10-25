package com.credit.bladi.controller.workflow;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.controller.dto.user.response.UserResponseDTO;
import com.credit.bladi.controller.dto.workflow.request.AddDemandCommentRequestDTO;
import com.credit.bladi.controller.dto.workflow.request.AdvanceDemandRequestDTO;
import com.credit.bladi.controller.dto.workflow.request.AssignDemandRequestDTO;
import com.credit.bladi.controller.dto.workflow.request.ClassifyDemandRequestDTO;
import com.credit.bladi.controller.dto.workflow.response.DemandAmortizationResponseDTO;
import com.credit.bladi.controller.dto.workflow.response.DemandAssignmentHistoryResponseDTO;
import com.credit.bladi.controller.dto.workflow.response.DemandCommentResponseDTO;
import com.credit.bladi.controller.dto.workflow.response.DemandHistoryEntryResponseDTO;
import com.credit.bladi.controller.dto.workflow.response.DemandPipelineStepResponseDTO;
import com.credit.bladi.controller.dto.workflow.response.DemandResponseDTO;
import com.credit.bladi.controller.dto.workflow.response.DemandStepResponseDTO;
import com.credit.bladi.exception.ErrorResponse;
import com.credit.bladi.model.enums.DemandStatus;
import com.credit.bladi.model.enums.StepDirection;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Demand Tracking", description = "Back-office APIs to track business credit demands "
    + "through the Workflow/Process/Step processing pipeline (Initiation, Instruction, Risk Analysis, "
    + "Decision, Guarantees)")
public interface IDemandController {
    @Operation(summary = "List demands (paginated)", description = "Most recently created first.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Paginated list of demands",
            content = @Content(schema = @Schema(implementation = CreditPage.class))),
        @ApiResponse(responseCode = "401", description = "Not authenticated",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions: MANAGER role required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<CreditPage<DemandResponseDTO>> getAllDemands(
        @Parameter(description = "Page number (1-based)", name = "page", example = "1")
        @Min(value = 1, message = "{validation.invalidPage}")
        @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,

        @Parameter(description = "Page size (max 50)", name = "size", example = "20")
        @Min(value = 1, message = "{validation.invalidSize}")
        @Max(value = 50, message = "{validation.invalidSize}")
        @RequestParam(name = "size", defaultValue = "20", required = false) Integer size,

        @Parameter(description = "Filter to only demands belonging to this Enterprise's portfolio (the "
            + "client-company view): the same id returned by GET /api/v1/enterprises. Omit for the "
            + "unfiltered list.", name = "enterpriseId", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @RequestParam(name = "enterpriseId", required = false) UUID enterpriseId,

        @Parameter(description = "Case-insensitive partial match on the client company name.",
            name = "companyName", example = "Atlas")
        @RequestParam(name = "companyName", required = false) String companyName,

        @Parameter(description = "Filter by derived status: IN_PROGRESS (not completed nor rejected), "
            + "COMPLETED or REJECTED.", name = "status", example = "IN_PROGRESS")
        @RequestParam(name = "status", required = false) DemandStatus status,

        @Parameter(description = "Minimum requested amount (MAD), inclusive.", name = "minAmount",
            example = "100000")
        @RequestParam(name = "minAmount", required = false) BigDecimal minAmount,

        @Parameter(description = "Maximum requested amount (MAD), inclusive.", name = "maxAmount",
            example = "5000000")
        @RequestParam(name = "maxAmount", required = false) BigDecimal maxAmount
    );

    @Operation(summary = "Get a single demand by id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Demand found",
            content = @Content(schema = @Schema(implementation = DemandResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "No demand found for the given ID",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DemandResponseDTO> getById(
        @Parameter(description = "Identifier of the demand.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @PathVariable UUID id
    );

    @Operation(summary = "Get a demand's current (or last, if completed) processing step")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Current step",
            content = @Content(schema = @Schema(implementation = DemandStepResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "No demand found for the given ID",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DemandStepResponseDTO> getCurrentStep(
        @Parameter(description = "Identifier of the demand.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @PathVariable UUID id
    );

    @Operation(summary = "Get a demand's full step-by-step processing history")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "History entries, oldest first",
            content = @Content(schema = @Schema(implementation = DemandHistoryEntryResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "No demand found for the given ID",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<List<DemandHistoryEntryResponseDTO>> getHistory(
        @Parameter(description = "Identifier of the demand.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @PathVariable UUID id
    );

    @Operation(summary = "Get a demand's full workflow pipeline",
        description = "Returns every step of the demand's workflow, in order, each flagged as DONE, "
            + "CURRENT or UPCOMING: meant to drive a step-by-step progress view (e.g. \"step 6 of 14\").")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ordered pipeline steps",
            content = @Content(schema = @Schema(implementation = DemandPipelineStepResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "No demand found for the given ID",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<List<DemandPipelineStepResponseDTO>> getPipeline(
        @Parameter(description = "Identifier of the demand.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @PathVariable UUID id
    );

    @Operation(
        summary = "Advance a demand to its next processing step",
        description = "Closes the demand's current step (only allowed if the caller's grade/team profile "
            + "matches the one required by that step) and opens the next one in its workflow's pipeline "
            + "(or the previous one, if returnReasonCode is set), assigned to assigneeUsername. That field "
            + "is required whenever a step is being opened: i.e. always, unless this call completes the "
            + "demand (advancing past its last step with no returnReasonCode), in which case there is no "
            + "step left to assign and it is ignored."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Demand advanced (or completed)",
            content = @Content(schema = @Schema(implementation = DemandResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Caller's grade/team does not match the one "
            + "required by the current step, or the chosen assigneeUsername's grade/team does not match "
            + "the one required by the step being opened",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "No demand found for the given ID",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Demand has already completed its pipeline",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "422", description = "assigneeUsername is missing while a step is "
            + "being opened",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DemandResponseDTO> advance(
        @Parameter(description = "Identifier of the demand to advance.",
            example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @PathVariable UUID id,
        @Valid @RequestBody AdvanceDemandRequestDTO requestDTO
    );

    @Operation(
        summary = "Classify a demand still at CLASSIFICATION_STEP",
        description = "The account officer's initial validation of a freshly-created demand with the "
            + "client. REAL attaches the demand to enterpriseId's portfolio and opens INITIATION_STEP "
            + "(assigned to assigneeUsername, required whenever INITIATION_STEP has a required grade/team). "
            + "FAKE/SIMULATION_ONLY reject the demand: it stops here, never joins a portfolio, and is "
            + "never completed in the usual sense."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Demand classified (rejected, or advanced into "
            + "INITIATION_STEP with its Enterprise set)",
            content = @Content(schema = @Schema(implementation = DemandResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Caller's grade/team does not match the one "
            + "required by CLASSIFICATION_STEP",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "No demand or Enterprise found for the given ID",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Demand is not currently at CLASSIFICATION_STEP",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "422", description = "enterpriseId missing while classifying REAL, or "
            + "assigneeUsername missing while INITIATION_STEP requires one",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DemandResponseDTO> classify(
        @Parameter(description = "Identifier of the demand to classify.",
            example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @PathVariable UUID id,
        @Valid @RequestBody ClassifyDemandRequestDTO requestDTO
    );

    @Operation(
        summary = "Get a demand's amortization schedule, at its current values or an explicit preview",
        description = "Computes the amortization schedule using the demand's amount, duration and "
            + "current applicable rate (set at creation and kept in sync by every POST /advance rate "
            + "decision) by default. Meant to be shown once the demand reaches the last step of its "
            + "pipeline. Optionally pass rate/amount/duration to preview the schedule at an arbitrary "
            + "triplet instead: e.g. to replay a past step's own appliedRate/appliedAmount/"
            + "appliedDuration from GET /{id}/history: without mutating the demand."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Amortization schedule",
            content = @Content(schema = @Schema(implementation = DemandAmortizationResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "No demand found for the given ID",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "This demand has no applicable rate yet and no "
            + "rate override was given: it must be advanced at least once first, or a rate must be "
            + "passed explicitly",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "503", description = "Enterprise credit parameters are not configured "
            + "for this demand's enterprise type",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DemandAmortizationResponseDTO> getAmortizationSchedule(
        @Parameter(description = "Identifier of the demand.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @PathVariable UUID id,
        @Parameter(description = "Rate (%) to preview instead of the demand's current applicable rate.",
            example = "7.25")
        @RequestParam(required = false) BigDecimal rate,
        @Parameter(description = "Amount (MAD) to preview instead of the demand's current amount.",
            example = "1500000.00")
        @RequestParam(required = false) BigDecimal amount,
        @Parameter(description = "Duration (months) to preview instead of the demand's current duration.",
            example = "84")
        @RequestParam(required = false) Integer duration
    );

    @Operation(
        summary = "Assign (or reassign) a demand's current step to a specific user",
        description = "Only a member of the team required by the current step may call this. The target "
            + "user must have the grade/team profile required by that step (\"same profile\" "
            + "Workflow Intégré reference decks, Assignment/Reassignment principle)."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Demand's current step, now assigned",
            content = @Content(schema = @Schema(implementation = DemandStepResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Caller or target user's grade/team does not "
            + "match the one required by the current step",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "No demand found for the given ID",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Demand has already completed its pipeline",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DemandStepResponseDTO> assign(
        @Parameter(description = "Identifier of the demand to assign.",
            example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @PathVariable UUID id,
        @Valid @RequestBody AssignDemandRequestDTO requestDTO
    );

    @Operation(
        summary = "List every (re)assignment made on a demand, across every step",
        description = "Oldest first. Each entry records the step, the previously assigned user (null on the "
            + "very first assignment of that step), the newly assigned user, who performed the (re)assignment "
            + "and when: so it stays fully traceable even when a step is reassigned several times before "
            + "being closed, unlike the current-step card which only ever shows the latest assignee."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Assignment history entries (possibly empty)",
            content = @Content(schema = @Schema(implementation = DemandAssignmentHistoryResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "No demand found for the given ID",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<List<DemandAssignmentHistoryResponseDTO>> getAssignmentHistory(
        @Parameter(description = "Identifier of the demand.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @PathVariable UUID id
    );

    @Operation(
        summary = "List users eligible to be assigned a step of the demand",
        description = "Returns MANAGER users whose grade/team profile matches the one required by the "
            + "targeted step: meant to populate an assignment dropdown. direction=CURRENT (default) "
            + "targets the demand's current step (for POST /assign); NEXT/PREVIOUS target whichever step "
            + "would be opened by POST /advance, for choosing its assigneeUsername ahead of time."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Eligible users (possibly empty, e.g. NEXT at "
            + "the last step, or PREVIOUS at the first)",
            content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "No demand found for the given ID",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<List<UserResponseDTO>> getEligibleAssignees(
        @Parameter(description = "Identifier of the demand.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @PathVariable UUID id,

        @Parameter(description = "Which step to target.", example = "CURRENT")
        @RequestParam(name = "direction", defaultValue = "CURRENT") StepDirection direction
    );

    @Operation(
        summary = "List remarks left on a demand",
        description = "Oldest first. Each remark records the step the demand was on when it was posted, "
            + "independent from advancing or (re)assigning that step."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Remarks (possibly empty)",
            content = @Content(schema = @Schema(implementation = DemandCommentResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "No demand found for the given ID",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<List<DemandCommentResponseDTO>> getComments(
        @Parameter(description = "Identifier of the demand.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @PathVariable UUID id
    );

    @Operation(
        summary = "Leave a remark on a demand's current step",
        description = "Does not advance or (re)assign the step: purely informational, e.g. to log that "
            + "the file is waiting on a document from the client."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Remark recorded",
            content = @Content(schema = @Schema(implementation = DemandCommentResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "No demand found for the given ID",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DemandCommentResponseDTO> addComment(
        @Parameter(description = "Identifier of the demand to comment on.",
            example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @PathVariable UUID id,
        @Valid @RequestBody AddDemandCommentRequestDTO requestDTO
    );
}
