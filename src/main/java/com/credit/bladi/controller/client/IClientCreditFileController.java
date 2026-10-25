package com.credit.bladi.controller.client;

import com.credit.bladi.controller.dto.servicing.request.DeclarePaymentRequestDTO;
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

@Tag(name = "Client Credit File", description = "CLIENT-facing: consult the credit file opened for "
    + "an accepted demand in my portfolio, disbursement, repayment schedule, and self-declare a "
    + "payment against one of its installments. See /api/v1/demands/{demandId}/credit-file for the "
    + "Manager-facing side.")
public interface IClientCreditFileController {
    @Operation(
        summary = "Get the credit file for a demand in my portfolio",
        description = "403 unless the current CLIENT has an APPROVED link to this demand's Enterprise."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Credit file found",
            content = @Content(schema = @Schema(implementation = CreditFileResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "No approved link to this demand's Enterprise",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "No demand found for the given ID, or it has not "
            + "completed its pipeline yet: no credit file has been opened for it",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<CreditFileResponseDTO> getByDemandId(
        @Parameter(description = "Identifier of the demand.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @PathVariable UUID demandId
    );

    @Operation(
        summary = "Self-declare having paid an installment in full (\"I paid\")",
        description = "No real payment gateway is integrated: this records the RIB the client says they "
            + "paid from and moves the installment to DECLARED. It is NOT marked PAID yet: the manager "
            + "managing the file is emailed/notified and must check it against the bank statement before "
            + "validating (or rejecting) it."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Installment marked DECLARED",
            content = @Content(schema = @Schema(implementation = InstallmentResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Missing or invalid sourceRib",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "No approved link to this demand's Enterprise",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "No demand/credit file/installment found for the "
            + "given IDs, or the installment does not belong to this demand's credit file",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "This installment has already been paid or already "
            + "has a payment declared and awaiting validation",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<InstallmentResponseDTO> payInstallment(
        @Parameter(description = "Identifier of the demand.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @PathVariable UUID demandId,

        @Parameter(description = "Identifier of the installment being paid.",
            example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @PathVariable UUID installmentId,

        @Valid @RequestBody DeclarePaymentRequestDTO request
    );
}
