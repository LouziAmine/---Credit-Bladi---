package com.credit.bladi.controller.dto.investment.request;

import com.credit.bladi.validator.ValidationConstraints;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

@Schema(
    name = "InvestmentCreditRequestDTO",
    description = """
        Request payload to simulate a business investment credit for a Petite Entreprise (PME) or
        Grande Entreprise (GE). Unlike the individual mortgage simulation, eligibility is not
        based on property value/LTV but on company age, financing-to-revenue ratio, guarantee
        coverage and debt-service ratio.
        """
)
public record InvestmentCreditRequestDTO(
    @Schema(description = "Company legal name.", example = "Atlas Textile SARL",
        requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{validation.investmentCredit.companyName.notBlank}")
    String companyName,

    @Schema(description = "Enterprise category. Must be one of: TPE, AUTO_ENTREPRENEUR, PME, ETI, GE.",
        example = "PME", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{validation.investmentCredit.enterpriseType.notBlank}")
    @Pattern(regexp = ValidationConstraints.ENTERPRISE_TYPE_REGEX,
        message = "{validation.investmentCredit.enterpriseType.pattern}")
    String enterpriseType,

    @Schema(description = "Economic sector (free text, captured for reporting).", example = "Textile")
    String sector,

    @Schema(description = "Contact email of the applicant, to reach them about this file.",
        example = "contact@atlas-textile.ma", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{validation.investmentCredit.email.notBlank}")
    @Email(message = "{validation.investmentCredit.email.pattern}")
    String email,

    @Schema(description = "Contact phone number of the applicant (Moroccan format).", example = "0612345678",
        requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{validation.investmentCredit.phone.notBlank}")
    @Pattern(regexp = "^(?:\\+212|0)[5-7]\\d{8}$", message = "{validation.investmentCredit.phone.pattern}")
    String phone,

    @Schema(
        description = "Requested financing amount in MAD. Must be between "
            + ValidationConstraints.ENTERPRISE_AMOUNT_MIN + " and " + ValidationConstraints.ENTERPRISE_AMOUNT_MAX
            + ".",
        example = "1500000.00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "{validation.investmentCredit.amount.notNull}")
    @DecimalMin(value = ValidationConstraints.ENTERPRISE_AMOUNT_MIN,
        message = "{validation.investmentCredit.amount.min}")
    @DecimalMax(value = ValidationConstraints.ENTERPRISE_AMOUNT_MAX,
        message = "{validation.investmentCredit.amount.max}")
    BigDecimal amount,

    @Schema(description = "Financing duration in months.", example = "84",
        requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "{validation.investmentCredit.duration.notNull}")
    @Min(value = ValidationConstraints.DURATION_MIN, message = "{validation.investmentCredit.duration.min}")
    @Max(value = ValidationConstraints.DURATION_MAX, message = "{validation.investmentCredit.duration.max}")
    Integer duration,

    @Schema(description = "Company annual revenue in MAD, used to cap the financing "
        + "amount.", example = "8000000.00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "{validation.investmentCredit.annualRevenue.notNull}")
    @DecimalMin(value = ValidationConstraints.ENTERPRISE_ANNUAL_AMOUNT_MIN, inclusive = false,
        message = "{validation.investmentCredit.annualRevenue.min}")
    @DecimalMax(value = ValidationConstraints.ENTERPRISE_ANNUAL_AMOUNT_MAX,
        message = "{validation.investmentCredit.annualRevenue.max}")
    BigDecimal annualRevenue,

    @Schema(description = "Company age in years since incorporation.", example = "5",
        requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "{validation.investmentCredit.companyAgeYears.notNull}")
    @Min(value = ValidationConstraints.ENTERPRISE_COMPANY_AGE_MIN,
        message = "{validation.investmentCredit.companyAgeYears.min}")
    @Max(value = ValidationConstraints.ENTERPRISE_COMPANY_AGE_MAX,
        message = "{validation.investmentCredit.companyAgeYears.max}")
    Integer companyAgeYears,

    @Schema(description = "Annual debt service of the company's existing debt in MAD (0 if none). Added to "
        + "the new loan's annual debt service to check the debt-service ratio.", example = "300000.00")
    @DecimalMin(value = ValidationConstraints.ENTERPRISE_ANNUAL_AMOUNT_MIN,
        message = "{validation.investmentCredit.existingAnnualDebtService.min}")
    @DecimalMax(value = ValidationConstraints.ENTERPRISE_ANNUAL_AMOUNT_MAX,
        message = "{validation.investmentCredit.existingAnnualDebtService.max}")
    BigDecimal existingAnnualDebtService,

    @Schema(description = "Company net annual result in MAD, used to check the debt-service "
        + "ratio.", example = "1200000.00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "{validation.investmentCredit.netAnnualResult.notNull}")
    @DecimalMin(value = ValidationConstraints.ENTERPRISE_ANNUAL_AMOUNT_MIN, inclusive = false,
        message = "{validation.investmentCredit.netAnnualResult.min}")
    @DecimalMax(value = ValidationConstraints.ENTERPRISE_ANNUAL_AMOUNT_MAX,
        message = "{validation.investmentCredit.netAnnualResult.max}")
    BigDecimal netAnnualResult,

    @Schema(description = "Value of the guarantees offered against this financing in MAD, used to check the "
        + "minimum guarantee coverage ratio.", example = "1000000.00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "{validation.investmentCredit.guaranteesValue.notNull}")
    @DecimalMin(value = ValidationConstraints.ENTERPRISE_ANNUAL_AMOUNT_MIN,
        message = "{validation.investmentCredit.guaranteesValue.min}")
    @DecimalMax(value = ValidationConstraints.ENTERPRISE_ANNUAL_AMOUNT_MAX,
        message = "{validation.investmentCredit.guaranteesValue.max}")
    BigDecimal guaranteesValue
) {}
