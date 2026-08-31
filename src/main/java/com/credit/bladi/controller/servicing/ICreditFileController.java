package com.credit.bladi.controller.servicing;

import com.credit.bladi.controller.dto.servicing.request.RejectPaymentRequestDTO;
import com.credit.bladi.controller.dto.servicing.response.CreditFileResponseDTO;
import com.credit.bladi.controller.dto.servicing.response.InstallmentResponseDTO;
import com.credit.bladi.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Credit File Tracking", description = "Back-office APIs to consult a demand's credit "
    + "file: the disbursement and repayment schedule opened automatically once the demand completes "
    + "its workflow pipeline: and to validate or reject a client's declared payment.")
public interface ICreditFileController {
    @Operation(summary = "Get the credit file opened for a demand",
        description = "Returns the disbursement and the full repayment schedule, each "
            + "installment carrying its live payment status (PENDING/DECLARED/PAID/LATE): a DECLARED "
            + "installment also carries the RIB the client says they paid from, to check before validating.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Credit file found",
            content = @Content(schema = @Schema(implementation = CreditFileResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "No demand found for the given ID, or the demand "
            + "has not completed its pipeline yet: no credit file has been opened for it",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<CreditFileResponseDTO> getByDemandId(
        @Parameter(description = "Identifier of the demand.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @PathVariable UUID demandId
    );

    @Operation(summary = "Validate a client's declared payment",
        description = "Confirms the declared payment actually landed on our account: marks the "
            + "installment PAID and notifies the client. Only valid on a DECLARED installment.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Installment marked PAID",
            content = @Content(schema = @Schema(implementation = InstallmentResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "No demand/credit file/installment found for the "
            + "given IDs, or the installment does not belong to this demand's credit file",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "No payment is DECLARED (awaiting validation) for "
            + "this installment", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<InstallmentResponseDTO> validatePayment(
        @Parameter(description = "Identifier of the demand.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @PathVariable UUID demandId,

        @Parameter(description = "Identifier of the installment.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @PathVariable UUID installmentId
    );

    @Operation(summary = "Reject a client's declared payment",
        description = "The declared RIB/amount could not be matched against the bank statement: reverts "
            + "the installment to PENDING/LATE and notifies the client with the given reason. Only valid "
            + "on a DECLARED installment.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Installment reverted to PENDING/LATE",
            content = @Content(schema = @Schema(implementation = InstallmentResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Missing or invalid reason",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "No demand/credit file/installment found for the "
            + "given IDs, or the installment does not belong to this demand's credit file",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "No payment is DECLARED (awaiting validation) for "
            + "this installment", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<InstallmentResponseDTO> rejectPayment(
        @Parameter(description = "Identifier of the demand.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @PathVariable UUID demandId,

        @Parameter(description = "Identifier of the installment.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @PathVariable UUID installmentId,

        @Valid @RequestBody RejectPaymentRequestDTO request
    );
}
