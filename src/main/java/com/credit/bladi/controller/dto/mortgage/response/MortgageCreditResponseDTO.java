package com.credit.bladi.controller.dto.mortgage.response;

import com.credit.bladi.controller.dto.AmortizationRowDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;

@Schema(
    name = "MortgageCreditResponseDTO",
    description = """
        Simulation result with applicable interest rates, monthly payments, and associated fees.

        Rate and payment fields are returned as a min/max range based on the borrower's profile.
        Exactly one of `conventional`/`mourabaha` is present, matching `productType`: see those
        schemas for the product-specific figures. All monetary values are in MAD.
        """
)
public record MortgageCreditResponseDTO(
    @Schema(description = "Requested loan amount (MAD).", example = "500000.00") BigDecimal amount,
    @Schema(description = "Loan duration (months).", example = "240") Integer duration,
    @Schema(description = "Socio-professional category code.", example = "SALA") String cspCode,
    @Schema(description = "Financing product that was simulated.", example = "CONVENTIONAL") String productType,
    @Schema(description = "Nationality code (ISO 3166-1 alpha-2).", example = "MA") String nationalityCode,
    @Schema(description = "Whether the borrower is a Moroccan resident.", example = "true") Boolean moroccanResident,
    @Schema(description = "Borrower age in years.", example = "55") Integer age,
    @Schema(description = "Household monthly income used to check the BAM 40% debt-ratio rule: "
        + "monthlyIncome + coBorrowerMonthlyIncome (0 if no co-borrower or not declared).", example = "20000.00")
    BigDecimal totalMonthlyIncome,
    @Schema(description = "Applicable annual interest rate (CONVENTIONAL) or profit rate (MOURABAHA) for this "
        + "CSP profile (strategy-computed).", example = "4.80") BigDecimal applicableRate,
    @Schema(description = "TAEG (Taux Annuel Effectif Global): effective annual cost of the credit, "
        + "including interest/profit margin, mandatory insurance and processing fees (excludes one-off "
        + "property acquisition costs: registration, land conservation, notary). Expressed as a percentage.",
        example = "5.74") BigDecimal taeg,
    @Schema(description = "Registration fees (MAD).", example = "7500.00") BigDecimal registrationFees,
    @Schema(description = "Land conservation fees (MAD).", example = "7500.00") BigDecimal landConservationFees,
    @Schema(description = "Notary fees (MAD).", example = "5000.00") BigDecimal notaryFees,
    @Schema(description = "Bank processing fees (MAD).", example = "5000.00") BigDecimal processingFees,
    @Schema(description = "Total fees (MAD).", example = "25000.00") BigDecimal totalFees,
    @Schema(description = "Conventional-only figures: present when productType is CONVENTIONAL, null "
        + "otherwise.") ConventionalFinancingResponseDTO conventional,
    @Schema(description = "Mourabaha-only figures: present when productType is MOURABAHA, null otherwise.")
    MourabahaFinancingResponseDTO mourabaha,
    @Schema(description = "Month-by-month amortization schedule at the applicable "
        + "rate/margin: not for the min/max bound rates, which are display-only.")
    List<AmortizationRowDTO> amortizationSchedule,
    @Schema(description = "Reference of the tracked dossier now entering the mortgage processing pipeline "
        + "(MORTGAGE_RETAIL workflow): see GET /api/v1/demands.", example = "DEM-3F2A9C1B") String demandReference
) {}
