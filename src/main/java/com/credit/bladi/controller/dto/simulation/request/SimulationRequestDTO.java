package com.credit.bladi.controller.dto.simulation.request;

import com.credit.bladi.validator.IsoCountryCode;
import com.credit.bladi.validator.ValidationConstraints;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Schema(
    name = "SimulationRequestDTO",
    description = """
        Request payload to simulate a Moroccan mortgage (crédit immobilier) according to BAM rules.

        Amount must be between 70 000 and 30 000 000 MAD.
        Duration must be between 12 and 300 months.
        """
)
public class SimulationRequestDTO {

    @Schema(
        description = "Loan amount in MAD. Must be between "
            + ValidationConstraints.AMOUNT_MIN + " and " + ValidationConstraints.AMOUNT_MAX + ".",
        example = "500000.00",
        requiredMode = Schema.RequiredMode.REQUIRED
    )

    @NotNull(message = "Amount is required")
    @DecimalMin(value = ValidationConstraints.AMOUNT_MIN,
        message = "Minimum amount is " + ValidationConstraints.AMOUNT_MIN)
    @DecimalMax(value = ValidationConstraints.AMOUNT_MAX,
        message = "Maximum amount is " + ValidationConstraints.AMOUNT_MAX)
    private BigDecimal amount;

    @Schema(
        description = "Loan duration in months. Must be between "
            + ValidationConstraints.DURATION_MIN + " and " + ValidationConstraints.DURATION_MAX + ".",
        example = "240",
        requiredMode = Schema.RequiredMode.REQUIRED
    )

    @NotNull(message = "Duration is required")
    @Min(value = ValidationConstraints.DURATION_MIN,
        message = "Minimum duration is " + ValidationConstraints.DURATION_MIN + " months")
    @Max(value = ValidationConstraints.DURATION_MAX,
        message = "Maximum duration is " + ValidationConstraints.DURATION_MAX + " months")
    private Integer duration;

    @Schema(
        description = "Socio-professional category code. Must be one of: RETR, SALA, FONC, PROF, INDE.",
        example = "SALA",
        requiredMode = Schema.RequiredMode.REQUIRED
    )

    @NotBlank(message = "cspCode is required")
    @Pattern(regexp = ValidationConstraints.CSP_CODE_REGEX,
        message = "cspCode must be one of: RETR, SALA, FONC, PROF, INDE")
    private String cspCode;

    @Schema(
        description = "Financing product. CONVENTIONAL is an interest-based BAM mortgage; MOURABAHA is a "
            + "participative financing (profit margin + VAT on margin, no interest). Defaults to CONVENTIONAL "
            + "if omitted.",
        example = "CONVENTIONAL"
    )
    @Pattern(regexp = ValidationConstraints.PRODUCT_TYPE_REGEX,
        message = "productType must be one of: CONVENTIONAL, MOURABAHA")
    private String productType;

    @Schema(
        description = "Nationality code following ISO 3166-1 alpha-2 format (e.g. MA, FR). Captured for "
            + "audit/reporting traceability only — it does not currently affect the applicable rate, LTV, "
            + "or debt-ratio calculation (no MRE-specific pricing rule is implemented).",
        example = "MA",
        requiredMode = Schema.RequiredMode.REQUIRED
    )

    @NotBlank(message = "Nationality code is required")
    @Pattern(regexp = ValidationConstraints.NATIONALITY_CODE_REGEX,
        message = "Nationality code must follow ISO 3166-1 alpha-2 format (e.g. MA, FR)")
    @IsoCountryCode
    private String nationalityCode;

    @Schema(
        description = "Whether the borrower is a Moroccan resident. Captured for audit/reporting "
            + "traceability only — it does not currently affect the applicable rate, LTV, or debt-ratio "
            + "calculation (no MRE-specific pricing rule is implemented).",
        example = "true",
        requiredMode = Schema.RequiredMode.REQUIRED
    )

    @NotNull(message = "moroccanResident is required")
    private Boolean moroccanResident;

    @Schema(
        description = "Property purchase price in MAD. Used to verify the minimum 10% deposit rule (BAM). "
            + "Must be between " + ValidationConstraints.PROPERTY_VALUE_MIN + " and "
            + ValidationConstraints.PROPERTY_VALUE_MAX + ".",
        example = "600000.00",
        requiredMode = Schema.RequiredMode.REQUIRED
    )

    @NotNull(message = "Property value is required")
    @DecimalMin(value = ValidationConstraints.PROPERTY_VALUE_MIN,
        message = "Minimum property value is " + ValidationConstraints.PROPERTY_VALUE_MIN + " MAD")
    @DecimalMax(value = ValidationConstraints.PROPERTY_VALUE_MAX,
        message = "Maximum property value is " + ValidationConstraints.PROPERTY_VALUE_MAX + " MAD")
    private BigDecimal propertyValue;

    @Schema(
        description = "Borrower monthly net income in MAD. Used to verify the maximum 40% debt ratio (BAM). "
            + "Must be between " + ValidationConstraints.MONTHLY_INCOME_MIN + " and "
            + ValidationConstraints.MONTHLY_INCOME_MAX + ".",
        example = "15000.00",
        requiredMode = Schema.RequiredMode.REQUIRED
    )

    @NotNull(message = "Monthly income is required")
    @DecimalMin(value = ValidationConstraints.MONTHLY_INCOME_MIN,
        message = "Minimum monthly income is " + ValidationConstraints.MONTHLY_INCOME_MIN + " MAD")
    @DecimalMax(value = ValidationConstraints.MONTHLY_INCOME_MAX,
        message = "Maximum monthly income is " + ValidationConstraints.MONTHLY_INCOME_MAX + " MAD")
    private BigDecimal monthlyIncome;

    @Schema(
        description = "Borrower current age in years. Used to enforce the BAM age-at-maturity rule "
            + "(age + duration in years must not exceed the configured maximum), applied to every CSP profile.",
        example = "55",
        requiredMode = Schema.RequiredMode.REQUIRED
    )

    @NotNull(message = "Age is required")
    @Min(value = ValidationConstraints.AGE_MIN,
        message = "Minimum age is " + ValidationConstraints.AGE_MIN)
    @Max(value = ValidationConstraints.AGE_MAX,
        message = "Maximum age is " + ValidationConstraints.AGE_MAX)
    private Integer age;

    @Schema(
        description = "Whether the loan has a co-borrower. Doubles the mandatory ADI "
            + "(death/disability insurance) premium (BAM regulation). Defaults to false if omitted.",
        example = "false"
    )
    private Boolean coBorrower;

    @Schema(
        description = "Co-borrower monthly net income in MAD, added to monthlyIncome to form the household "
            + "income the maximum 40% debt ratio (BAM) is checked against. Only taken into account when "
            + "coBorrower is true; ignored otherwise. Defaults to 0 if omitted.",
        example = "10000.00"
    )
    @DecimalMin(value = ValidationConstraints.MONTHLY_INCOME_MIN,
        message = "Minimum co-borrower monthly income is " + ValidationConstraints.MONTHLY_INCOME_MIN + " MAD")
    @DecimalMax(value = ValidationConstraints.MONTHLY_INCOME_MAX,
        message = "Maximum co-borrower monthly income is " + ValidationConstraints.MONTHLY_INCOME_MAX + " MAD")
    private BigDecimal coBorrowerMonthlyIncome;

    public BigDecimal getAmount() {
        return amount;
    }

    public Integer getDuration() {
        return duration;
    }

    public String getCspCode() {
        return cspCode;
    }

    public String getProductType() {
        return productType;
    }

    public String getNationalityCode() {
        return nationalityCode;
    }

    public Boolean getMoroccanResident() {
        return moroccanResident;
    }

    public BigDecimal getPropertyValue() {
        return propertyValue;
    }

    public BigDecimal getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public void setCspCode(String cspCode) {
        this.cspCode = cspCode;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public void setNationalityCode(String nationalityCode) {
        this.nationalityCode = nationalityCode;
    }

    public void setMoroccanResident(Boolean moroccanResident) {
        this.moroccanResident = moroccanResident;
    }

    public void setPropertyValue(BigDecimal propertyValue) {
        this.propertyValue = propertyValue;
    }

    public void setMonthlyIncome(BigDecimal monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Boolean getCoBorrower() {
        return coBorrower;
    }

    public void setCoBorrower(Boolean coBorrower) {
        this.coBorrower = coBorrower;
    }

    public BigDecimal getCoBorrowerMonthlyIncome() {
        return coBorrowerMonthlyIncome;
    }

    public void setCoBorrowerMonthlyIncome(BigDecimal coBorrowerMonthlyIncome) {
        this.coBorrowerMonthlyIncome = coBorrowerMonthlyIncome;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof SimulationRequestDTO && EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return String.format(
            "SimulationRequestDTO [amount=%s, duration=%s, cspCode=%s, productType=%s, nationalityCode=%s, "
                + "moroccanResident=%s, propertyValue=%s, monthlyIncome=%s, coBorrower=%s, "
                + "coBorrowerMonthlyIncome=%s]",
            amount, duration, cspCode, productType, nationalityCode, moroccanResident, propertyValue, monthlyIncome,
            coBorrower, coBorrowerMonthlyIncome);
    }
}