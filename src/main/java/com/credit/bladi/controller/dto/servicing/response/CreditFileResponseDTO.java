package com.credit.bladi.controller.dto.servicing.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(
    name = "CreditFileResponseDTO",
    description = "The credit file opened automatically once a demand completes its workflow pipeline: the "
        + "accepted demand, the initial disbursement, and the full repayment schedule with "
        + "each installment's live payment status."
)
public record CreditFileResponseDTO(
    @Schema(description = "Record identifier.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b") UUID id,
    @Schema(description = "Identifier of the originating demand.",
        example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b") UUID demandId,
    @Schema(description = "Human-readable demand reference.", example = "DEM-3F2A9C1B") String demandReference,
    @Schema(description = "Client company name.", example = "Atlas Textile SARL") String companyName,
    @Schema(description = "Human-readable credit file reference.", example = "DOS-3F2A9C1B") String reference,
    @Schema(description = "When the funds were disbursed: the demand's completion date.")
    Instant disbursedAt,
    @Schema(description = "Disbursed amount (MAD).", example = "1500000.00") BigDecimal disbursedAmount,
    @Schema(description = "ACTIVE while installments remain unpaid, CLOSED once the credit is fully repaid.",
        example = "ACTIVE") String status,
    @Schema(description = "Full repayment schedule, oldest installment first.")
    List<InstallmentResponseDTO> installments,
    @Schema(description = "Beneficiary name to transfer monthly installments to.", example = "Credit Bladi SA")
    String beneficiaryName,
    @Schema(description = "Beneficiary RIB to transfer monthly installments to: shown to the client, and to the "
        + "manager for reconciliation against a declared payment.", example = "230 780 0123456789012345 95")
    String beneficiaryRib,
    @Schema(description = "The client's RIB of record, captured when the case was classified (null if "
        + "never captured): used as the default when declaring a payment; not authoritative, the client "
        + "can still declare from a different RIB.", example = "230 780 0123456789012345 95") String clientRib
) {}
