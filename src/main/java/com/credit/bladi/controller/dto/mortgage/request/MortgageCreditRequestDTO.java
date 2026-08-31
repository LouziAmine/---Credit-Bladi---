package com.credit.bladi.controller.dto.mortgage.request;

import com.credit.bladi.validator.IsoCountryCode;
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
    name = "MortgageCreditRequestDTO",
    description = """
        Request payload to simulate a Moroccan mortgage according to BAM rules.

        Amount must be between 70 000 and 30 000 000 MAD.
        Duration must be between 12 and 300 months.
        """
)
public record MortgageCreditRequestDTO(
    @Schema(
        description = "Applicant's full name, to reach them about this file if the simulation is eligible.",
        example = "Karim Benali",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "{validation.mortgageCredit.applicantName.notBlank}")
    String applicantName,

    @Schema(
        description = "Contact email of the applicant, to reach them about this file.",
        example = "karim.benali@example.ma",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "{validation.mortgageCredit.email.notBlank}")
    @Email(message = "{validation.mortgageCredit.email.pattern}")
    String email,

    @Schema(
        description = "Contact phone number of the applicant (Moroccan format).",
        example = "0612345678",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "{validation.mortgageCredit.phone.notBlank}")
    @Pattern(regexp = "^(?:\\+212|0)[5-7]\\d{8}$", message = "{validation.mortgageCredit.phone.pattern}")
    String phone,

    @Schema(
        description = "Loan amount in MAD. Must be between "
            + ValidationConstraints.AMOUNT_MIN + " and " + ValidationConstraints.AMOUNT_MAX + ".",
        example = "500000.00",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "{validation.mortgageCredit.amount.notNull}")
    @DecimalMin(value = ValidationConstraints.AMOUNT_MIN, message = "{validation.mortgageCredit.amount.min}")
    @DecimalMax(value = ValidationConstraints.AMOUNT_MAX, message = "{validation.mortgageCredit.amount.max}")
    BigDecimal amount,

    @Schema(
        description = "Loan duration in months. Must be between "
            + ValidationConstraints.DURATION_MIN + " and " + ValidationConstraints.DURATION_MAX + ".",
        example = "240",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "{validation.mortgageCredit.duration.notNull}")
    @Min(value = ValidationConstraints.DURATION_MIN, message = "{validation.mortgageCredit.duration.min}")
    @Max(value = ValidationConstraints.DURATION_MAX, message = "{validation.mortgageCredit.duration.max}")
    Integer duration,

    @Schema(
        description = "Socio-professional category code. Must be one of: RETR, SALA, FONC, PROF, INDE.",
        example = "SALA",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "{validation.mortgageCredit.cspCode.notBlank}")
    @Pattern(regexp = ValidationConstraints.CSP_CODE_REGEX, message = "{validation.mortgageCredit.cspCode.pattern}")
    String cspCode,

    @Schema(
        description = "Financing product. CONVENTIONAL is an interest-based BAM mortgage; MOURABAHA is a "
            + "participative financing (profit margin + VAT on margin, no interest). Defaults to CONVENTIONAL "
            + "if omitted.",
        example = "CONVENTIONAL"
    )
    @Pattern(regexp = ValidationConstraints.PRODUCT_TYPE_REGEX,
        message = "{validation.mortgageCredit.productType.pattern}")
    String productType,

    @Schema(
        description = "Nationality code following ISO 3166-1 alpha-2 format (e.g. MA, FR). Captured for "
            + "audit/reporting traceability only: it does not currently affect the applicable rate, LTV, "
            + "or debt-ratio calculation (no MRE-specific pricing rule is implemented).",
        example = "MA",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "{validation.mortgageCredit.nationalityCode.notBlank}")
    @Pattern(regexp = ValidationConstraints.NATIONALITY_CODE_REGEX,
        message = "{validation.mortgageCredit.nationalityCode.pattern}")
    @IsoCountryCode
    String nationalityCode,

    @Schema(
        description = "Whether the borrower is a Moroccan resident. Captured for audit/reporting "
            + "traceability only: it does not currently affect the applicable rate, LTV, or debt-ratio "
            + "calculation (no MRE-specific pricing rule is implemented).",
        example = "true",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "{validation.mortgageCredit.moroccanResident.notNull}")
    Boolean moroccanResident,

    @Schema(
        description = "Property purchase price in MAD. Used to verify the minimum 10% deposit rule (BAM). "
            + "Must be between " + ValidationConstraints.PROPERTY_VALUE_MIN + " and "
            + ValidationConstraints.PROPERTY_VALUE_MAX + ".",
        example = "600000.00",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "{validation.mortgageCredit.propertyValue.notNull}")
    @DecimalMin(value = ValidationConstraints.PROPERTY_VALUE_MIN,
        message = "{validation.mortgageCredit.propertyValue.min}")
    @DecimalMax(value = ValidationConstraints.PROPERTY_VALUE_MAX,
        message = "{validation.mortgageCredit.propertyValue.max}")
    BigDecimal propertyValue,

    @Schema(
        description = "Borrower monthly net income in MAD. Used to verify the maximum 40% debt ratio (BAM). "
            + "Must be between " + ValidationConstraints.MONTHLY_INCOME_MIN + " and "
            + ValidationConstraints.MONTHLY_INCOME_MAX + ".",
        example = "15000.00",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "{validation.mortgageCredit.monthlyIncome.notNull}")
    @DecimalMin(value = ValidationConstraints.MONTHLY_INCOME_MIN,
        message = "{validation.mortgageCredit.monthlyIncome.min}")
    @DecimalMax(value = ValidationConstraints.MONTHLY_INCOME_MAX,
        message = "{validation.mortgageCredit.monthlyIncome.max}")
    BigDecimal monthlyIncome,

    @Schema(
        description = "Borrower current age in years. Used to enforce the BAM age-at-maturity rule "
            + "(age + duration in years must not exceed the configured maximum), applied to every CSP profile.",
        example = "55",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "{validation.mortgageCredit.age.notNull}")
    @Min(value = ValidationConstraints.AGE_MIN, message = "{validation.mortgageCredit.age.min}")
    @Max(value = ValidationConstraints.AGE_MAX, message = "{validation.mortgageCredit.age.max}")
    Integer age,

    @Schema(
        description = "Whether the loan has a co-borrower. Doubles the mandatory ADI "
            + "(death/disability insurance) premium (BAM regulation). Defaults to false if omitted.",
        example = "false"
    )
    Boolean coBorrower,

    @Schema(
        description = "Co-borrower monthly net income in MAD, added to monthlyIncome to form the household "
            + "income the maximum 40% debt ratio (BAM) is checked against. Only taken into account when "
            + "coBorrower is true; ignored otherwise. Defaults to 0 if omitted.",
        example = "10000.00"
    )
    @DecimalMin(value = ValidationConstraints.MONTHLY_INCOME_MIN,
        message = "{validation.mortgageCredit.coBorrowerMonthlyIncome.min}")
    @DecimalMax(value = ValidationConstraints.MONTHLY_INCOME_MAX,
        message = "{validation.mortgageCredit.coBorrowerMonthlyIncome.max}")
    BigDecimal coBorrowerMonthlyIncome
) {}
