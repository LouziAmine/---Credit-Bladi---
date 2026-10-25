package com.credit.bladi.controller.servicing;

import com.credit.bladi.controller.dto.servicing.request.EarlyRepaymentRequestDTO;
import com.credit.bladi.controller.dto.servicing.response.EarlyRepaymentQuoteResponseDTO;
import com.credit.bladi.controller.dto.servicing.response.EarlyRepaymentResponseDTO;
import com.credit.bladi.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Early Repayment", description = "Remboursement anticipé (total or partial) of an active credit file: "
    + "quote, record a repayment received by the bank, and history. Indemnity capped per loi 31-08; late or "
    + "awaiting-validation installments must be settled first; not available for Mourabaha.")
public interface IEarlyRepaymentController {
    @Operation(summary = "Quote an early repayment made today")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Quote computed",
            content = @Content(schema = @Schema(implementation = EarlyRepaymentQuoteResponseDTO.class))),
        @ApiResponse(responseCode = "409", description = "Credit file closed, or installments in arrears",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "422", description = "Amount above the outstanding capital, or Mourabaha",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<EarlyRepaymentQuoteResponseDTO> quote(
        @Parameter(description = "Identifier of the demand.") @PathVariable UUID demandId,
        @Parameter(description = "Capital to repay (MAD); omit for a total repayment.", example = "100000.00")
        @DecimalMin(value = "0.01", message = "{validation.earlyRepayment.amount.min}")
        @RequestParam(name = "amount", required = false) BigDecimal amount
    );

    @Operation(summary = "Record an early repayment received by the bank",
        description = "Total: every remaining installment becomes REPAID_EARLY and the credit file is closed. "
            + "Partial: remaining installments keep their due dates with a lower amount. The client is notified.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Early repayment recorded",
            content = @Content(schema = @Schema(implementation = EarlyRepaymentResponseDTO.class))),
        @ApiResponse(responseCode = "409", description = "Credit file closed, or installments in arrears",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "422", description = "Amount above the outstanding capital, or Mourabaha",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<EarlyRepaymentResponseDTO> repay(
        @Parameter(description = "Identifier of the demand.") @PathVariable UUID demandId,
        @Valid @RequestBody EarlyRepaymentRequestDTO request
    );

    @Operation(summary = "List the early repayments recorded on the credit file")
    ResponseEntity<List<EarlyRepaymentResponseDTO>> history(
        @Parameter(description = "Identifier of the demand.") @PathVariable UUID demandId
    );
}
