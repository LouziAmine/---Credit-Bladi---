package com.credit.bladi.controller.dto.workflow.response;

import com.credit.bladi.controller.dto.AmortizationRowDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Schema(
    name = "DemandAmortizationResponseDTO",
    description = "Amortization schedule for a demand, computed at its current "
        + "applicable rate: enterprise or mortgage-Conventional (interest-based) or mortgage-Mourabaha "
        + "(margin-based). Exactly one of `enterprise`/`conventional`/`mourabaha` is present, matching the "
        + "demand's type and (for MORTGAGE) productType. All monetary values are in MAD."
)
public record DemandAmortizationResponseDTO(
    @Schema(description = "Identifier of the demand.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
    UUID demandId,
    @Schema(description = "Human-readable demand reference.", example = "DEM-3F2A9C1B") String reference,
    @Schema(description = "Financing amount (MAD).", example = "1500000.00") BigDecimal amount,
    @Schema(description = "Financing duration (months).", example = "84") Integer duration,
    @Schema(description = "Rate the schedule was computed at (the demand's current applicable rate, %). "
        + "For a mortgage-Mourabaha demand this is the profit rate (HT), not the TTC rate the "
        + "installment is actually computed at.", example = "7.25") BigDecimal applicableRate,
    @Schema(description = "Bank processing fees (MAD).", example = "15000.00") BigDecimal processingFees,
    @Schema(description = "TAEG (Taux Annuel Effectif Global), as a percentage.", example = "7.12") BigDecimal taeg,
    @Schema(description = "Month-by-month amortization schedule at the applicable rate/margin.")
    List<AmortizationRowDTO> amortizationSchedule,
    @Schema(description = "Monthly insurance premium, flat over the financing duration (MAD): ADI/dommages for "
        + "enterprise credit and mortgage-Conventional, Takaful (Islamic cooperative insurance) contribution for "
        + "mortgage-Mourabaha.", example = "1500.00") BigDecimal insurancePremium,
    @Schema(description = "Monthly payment including the insurance premium (monthlyPayment + "
        + "insurancePremium, MAD): this is the amount actually folded into the TAEG.",
        example = "23686.45") BigDecimal allInMonthlyPayment,
    @Schema(description = "Total insurance cost over the financing duration (insurancePremium x duration, MAD).",
        example = "126000.00") BigDecimal totalInsuranceCost,
    @Schema(description = "Enterprise-investment-credit-specific figures: present when the demand is "
        + "ENTERPRISE, null otherwise.") EnterpriseAmortizationDetailResponseDTO enterprise,
    @Schema(description = "Mortgage-Conventional-specific figures: present when the demand is MORTGAGE "
        + "with productType CONVENTIONAL, null otherwise.")
    ConventionalAmortizationDetailResponseDTO conventional,
    @Schema(description = "Mortgage-Mourabaha-specific figures: present when the demand is MORTGAGE with "
        + "productType MOURABAHA, null otherwise.") MourabahaAmortizationDetailResponseDTO mourabaha
) {}
