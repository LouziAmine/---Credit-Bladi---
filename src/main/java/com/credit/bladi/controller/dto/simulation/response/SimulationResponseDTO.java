package com.credit.bladi.controller.dto.simulation.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Schema(
    name = "SimulationResponseDTO",
    description = """
        Simulation result with applicable interest rates, monthly payments, and associated fees.

        Rate and payment fields are returned as a min/max range based on the borrower's profile.
        All monetary values are in MAD.
        """
)
public class SimulationResponseDTO {

    @Schema(description = "Requested loan amount (MAD).", example = "500000.00")
    private BigDecimal amount;

    @Schema(description = "Loan duration (months).", example = "240")
    private Integer duration;

    @Schema(description = "Socio-professional category code.", example = "SALA")
    private String cspCode;

    @Schema(description = "Financing product that was simulated.", example = "CONVENTIONAL")
    private String productType;

    @Schema(description = "Nationality code (ISO 3166-1 alpha-2).", example = "MA")
    private String nationalityCode;

    @Schema(description = "Whether the borrower is a Moroccan resident.", example = "true")
    private Boolean moroccanResident;

    @Schema(description = "Borrower age in years.", example = "55")
    private Integer age;

    @Schema(description = "Household monthly income used to check the BAM 40% debt-ratio rule: "
        + "monthlyIncome + coBorrowerMonthlyIncome (0 if no co-borrower or not declared).", example = "20000.00")
    private BigDecimal totalMonthlyIncome;

    @Schema(description = "Applicable annual interest rate for this CSP profile (strategy-computed).", example = "4.80")
    private BigDecimal applicableRate;

    @Schema(description = "Minimum applicable annual interest rate.", example = "3.50")
    private BigDecimal rateMin;

    @Schema(description = "Maximum applicable annual interest rate.", example = "5.50")
    private BigDecimal rateMax;

    @Schema(description = "Estimated monthly payment at the rate actually applicable to this borrower "
        + "(applicableRate), excluding ADI insurance (MAD).", example = "3244.79")
    private BigDecimal monthlyPayment;

    @Schema(description = "ADI (death/disability insurance) monthly premium at the rate actually applicable "
        + "to this borrower, mandatory BAM regulation (MAD).", example = "200.00")
    private BigDecimal adiMonthlyPremium;

    @Schema(description = "All-in monthly payment (monthlyPayment + ADI premium) at the rate actually "
        + "applicable to this borrower — the figure checked against the BAM debt-ratio rule (MAD).",
        example = "3444.79")
    private BigDecimal allInMonthlyPayment;

    @Schema(description = "Total interest over the loan duration at the rate actually applicable to this "
        + "borrower (MAD).", example = "278749.60")
    private BigDecimal totalInterest;

    @Schema(description = "Total ADI insurance cost over the loan duration at the rate actually applicable "
        + "to this borrower (MAD).", example = "48000.00")
    private BigDecimal totalAdiCost;

    @Schema(description = "Total repayment amount (principal + interest, excluding closing fees and ADI "
        + "insurance) at the rate actually applicable to this borrower (MAD).", example = "778749.60")
    private BigDecimal totalCost;

    @Schema(description = "TAEG (Taux Annuel Effectif Global) — effective annual cost of the credit, "
        + "including interest, mandatory ADI insurance and processing fees (excludes one-off property "
        + "acquisition costs: registration, land conservation, notary). Expressed as a percentage.",
        example = "5.74")
    private BigDecimal taeg;

    @Schema(description = "Minimum estimated monthly payment, excluding ADI insurance (MAD).", example = "2800.00")
    private BigDecimal monthlyPaymentMin;

    @Schema(description = "Maximum estimated monthly payment, excluding ADI insurance (MAD).", example = "3200.00")
    private BigDecimal monthlyPaymentMax;

    @Schema(description = "Minimum ADI (death/disability insurance) monthly premium, mandatory BAM regulation (MAD).",
        example = "200.00")
    private BigDecimal adiMonthlyPremiumMin;

    @Schema(description = "Maximum ADI (death/disability insurance) monthly premium, mandatory BAM regulation (MAD).",
        example = "232.00")
    private BigDecimal adiMonthlyPremiumMax;

    @Schema(description = "Minimum all-in monthly payment (monthlyPayment + ADI premium) — the figure checked "
        + "against the BAM debt-ratio rule (MAD).", example = "3000.00")
    private BigDecimal allInMonthlyPaymentMin;

    @Schema(description = "Maximum all-in monthly payment (monthlyPayment + ADI premium) — the figure checked "
        + "against the BAM debt-ratio rule (MAD).", example = "3432.00")
    private BigDecimal allInMonthlyPaymentMax;

    @Schema(description = "Minimum total interest over the loan duration (MAD).", example = "72000.00")
    private BigDecimal totalInterestMin;

    @Schema(description = "Maximum total interest over the loan duration (MAD).", example = "168000.00")
    private BigDecimal totalInterestMax;

    @Schema(description = "Minimum total ADI insurance cost over the loan duration (MAD).", example = "48000.00")
    private BigDecimal totalAdiCostMin;

    @Schema(description = "Maximum total ADI insurance cost over the loan duration (MAD).", example = "55680.00")
    private BigDecimal totalAdiCostMax;

    @Schema(description = "Minimum total repayment amount (principal + interest, excluding closing fees and ADI "
        + "insurance). See totalAdiCostMin/totalFees for those.", example = "572000.00")
    private BigDecimal totalCostMin;

    @Schema(description = "Maximum total repayment amount (principal + interest, excluding closing fees and ADI "
        + "insurance). See totalAdiCostMax/totalFees for those.", example = "668000.00")
    private BigDecimal totalCostMax;

    @Schema(description = "Registration fees (MAD).", example = "7500.00")
    private BigDecimal registrationFees;

    @Schema(description = "Land conservation fees (MAD).", example = "7500.00")
    private BigDecimal landConservationFees;

    @Schema(description = "Notary fees (MAD).", example = "5000.00")
    private BigDecimal notaryFees;

    @Schema(description = "Bank processing fees (MAD).", example = "5000.00")
    private BigDecimal processingFees;

    @Schema(description = "Total fees (MAD).", example = "25000.00")
    private BigDecimal totalFees;

    @Schema(description = "Mourabaha only — profit margin excluding VAT (Marge HT) over the financing duration "
        + "(MAD).", example = "33534.55")
    private BigDecimal marginHt;

    @Schema(description = "Mourabaha only — VAT applied to the profit margin (TVA sur Marge) (MAD).",
        example = "3353.45")
    private BigDecimal vatOnMargin;

    @Schema(description = "Mourabaha only — profit margin including VAT (Marge TTC) (MAD).", example = "36888.00")
    private BigDecimal marginTtc;

    @Schema(description = "Mourabaha only — flat monthly installment including VAT (échéance TTC) (MAD).",
        example = "4614.80")
    private BigDecimal monthlyPaymentTtc;

    @Schema(description = "Month-by-month amortization schedule (tableau d'amortissement) at the applicable "
        + "rate/margin — not for the min/max bound rates, which are display-only.")
    private List<AmortizationRowDTO> amortizationSchedule;

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

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getNationalityCode() {
        return nationalityCode;
    }

    public Boolean getMoroccanResident() {
        return moroccanResident;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public BigDecimal getTotalMonthlyIncome() {
        return totalMonthlyIncome;
    }

    public void setTotalMonthlyIncome(BigDecimal totalMonthlyIncome) {
        this.totalMonthlyIncome = totalMonthlyIncome;
    }

    public BigDecimal getApplicableRate() {
        return applicableRate;
    }

    public BigDecimal getRateMin() {
        return rateMin;
    }

    public BigDecimal getRateMax() {
        return rateMax;
    }

    public BigDecimal getMonthlyPayment() {
        return monthlyPayment;
    }

    public BigDecimal getAdiMonthlyPremium() {
        return adiMonthlyPremium;
    }

    public BigDecimal getAllInMonthlyPayment() {
        return allInMonthlyPayment;
    }

    public BigDecimal getTotalInterest() {
        return totalInterest;
    }

    public BigDecimal getTotalAdiCost() {
        return totalAdiCost;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public BigDecimal getTaeg() {
        return taeg;
    }

    public BigDecimal getMonthlyPaymentMin() {
        return monthlyPaymentMin;
    }

    public BigDecimal getMonthlyPaymentMax() {
        return monthlyPaymentMax;
    }

    public BigDecimal getAdiMonthlyPremiumMin() {
        return adiMonthlyPremiumMin;
    }

    public BigDecimal getAdiMonthlyPremiumMax() {
        return adiMonthlyPremiumMax;
    }

    public BigDecimal getAllInMonthlyPaymentMin() {
        return allInMonthlyPaymentMin;
    }

    public BigDecimal getAllInMonthlyPaymentMax() {
        return allInMonthlyPaymentMax;
    }

    public BigDecimal getTotalInterestMin() {
        return totalInterestMin;
    }

    public BigDecimal getTotalInterestMax() {
        return totalInterestMax;
    }

    public BigDecimal getTotalAdiCostMin() {
        return totalAdiCostMin;
    }

    public BigDecimal getTotalAdiCostMax() {
        return totalAdiCostMax;
    }

    public BigDecimal getTotalCostMin() {
        return totalCostMin;
    }

    public BigDecimal getTotalCostMax() {
        return totalCostMax;
    }

    public BigDecimal getRegistrationFees() {
        return registrationFees;
    }

    public BigDecimal getLandConservationFees() {
        return landConservationFees;
    }

    public BigDecimal getNotaryFees() {
        return notaryFees;
    }

    public BigDecimal getProcessingFees() {
        return processingFees;
    }

    public BigDecimal getTotalFees() {
        return totalFees;
    }

    public BigDecimal getMarginHt() {
        return marginHt;
    }

    public void setMarginHt(BigDecimal marginHt) {
        this.marginHt = marginHt;
    }

    public BigDecimal getVatOnMargin() {
        return vatOnMargin;
    }

    public void setVatOnMargin(BigDecimal vatOnMargin) {
        this.vatOnMargin = vatOnMargin;
    }

    public BigDecimal getMarginTtc() {
        return marginTtc;
    }

    public void setMarginTtc(BigDecimal marginTtc) {
        this.marginTtc = marginTtc;
    }

    public BigDecimal getMonthlyPaymentTtc() {
        return monthlyPaymentTtc;
    }

    public void setMonthlyPaymentTtc(BigDecimal monthlyPaymentTtc) {
        this.monthlyPaymentTtc = monthlyPaymentTtc;
    }

    public List<AmortizationRowDTO> getAmortizationSchedule() {
        return amortizationSchedule;
    }

    public void setAmortizationSchedule(List<AmortizationRowDTO> amortizationSchedule) {
        this.amortizationSchedule = amortizationSchedule;
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

    public void setNationalityCode(String nationalityCode) {
        this.nationalityCode = nationalityCode;
    }

    public void setMoroccanResident(Boolean moroccanResident) {
        this.moroccanResident = moroccanResident;
    }

    public void setApplicableRate(BigDecimal applicableRate) {
        this.applicableRate = applicableRate;
    }

    public void setRateMin(BigDecimal rateMin) {
        this.rateMin = rateMin;
    }

    public void setRateMax(BigDecimal rateMax) {
        this.rateMax = rateMax;
    }

    public void setMonthlyPayment(BigDecimal monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }

    public void setAdiMonthlyPremium(BigDecimal adiMonthlyPremium) {
        this.adiMonthlyPremium = adiMonthlyPremium;
    }

    public void setAllInMonthlyPayment(BigDecimal allInMonthlyPayment) {
        this.allInMonthlyPayment = allInMonthlyPayment;
    }

    public void setTotalInterest(BigDecimal totalInterest) {
        this.totalInterest = totalInterest;
    }

    public void setTotalAdiCost(BigDecimal totalAdiCost) {
        this.totalAdiCost = totalAdiCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public void setTaeg(BigDecimal taeg) {
        this.taeg = taeg;
    }

    public void setMonthlyPaymentMin(BigDecimal monthlyPaymentMin) {
        this.monthlyPaymentMin = monthlyPaymentMin;
    }

    public void setMonthlyPaymentMax(BigDecimal monthlyPaymentMax) {
        this.monthlyPaymentMax = monthlyPaymentMax;
    }

    public void setAdiMonthlyPremiumMin(BigDecimal adiMonthlyPremiumMin) {
        this.adiMonthlyPremiumMin = adiMonthlyPremiumMin;
    }

    public void setAdiMonthlyPremiumMax(BigDecimal adiMonthlyPremiumMax) {
        this.adiMonthlyPremiumMax = adiMonthlyPremiumMax;
    }

    public void setAllInMonthlyPaymentMin(BigDecimal allInMonthlyPaymentMin) {
        this.allInMonthlyPaymentMin = allInMonthlyPaymentMin;
    }

    public void setAllInMonthlyPaymentMax(BigDecimal allInMonthlyPaymentMax) {
        this.allInMonthlyPaymentMax = allInMonthlyPaymentMax;
    }

    public void setTotalInterestMin(BigDecimal totalInterestMin) {
        this.totalInterestMin = totalInterestMin;
    }

    public void setTotalInterestMax(BigDecimal totalInterestMax) {
        this.totalInterestMax = totalInterestMax;
    }

    public void setTotalAdiCostMin(BigDecimal totalAdiCostMin) {
        this.totalAdiCostMin = totalAdiCostMin;
    }

    public void setTotalAdiCostMax(BigDecimal totalAdiCostMax) {
        this.totalAdiCostMax = totalAdiCostMax;
    }

    public void setTotalCostMin(BigDecimal totalCostMin) {
        this.totalCostMin = totalCostMin;
    }

    public void setTotalCostMax(BigDecimal totalCostMax) {
        this.totalCostMax = totalCostMax;
    }

    public void setRegistrationFees(BigDecimal registrationFees) {
        this.registrationFees = registrationFees;
    }

    public void setLandConservationFees(BigDecimal landConservationFees) {
        this.landConservationFees = landConservationFees;
    }

    public void setNotaryFees(BigDecimal notaryFees) {
        this.notaryFees = notaryFees;
    }

    public void setProcessingFees(BigDecimal processingFees) {
        this.processingFees = processingFees;
    }

    public void setTotalFees(BigDecimal totalFees) {
        this.totalFees = totalFees;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof SimulationResponseDTO && EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return String.format(
            "SimulationResponseDTO [amount=%s, duration=%s, cspCode=%s, productType=%s, nationalityCode=%s, "
                + "moroccanResident=%s, age=%s, totalMonthlyIncome=%s, "
                + "applicableRate=%s, rateMin=%s, rateMax=%s, "
                + "monthlyPayment=%s, adiMonthlyPremium=%s, allInMonthlyPayment=%s, "
                + "totalInterest=%s, totalAdiCost=%s, totalCost=%s, taeg=%s, "
                + "monthlyPaymentMin=%s, monthlyPaymentMax=%s, "
                + "adiMonthlyPremiumMin=%s, adiMonthlyPremiumMax=%s, "
                + "allInMonthlyPaymentMin=%s, allInMonthlyPaymentMax=%s, "
                + "totalInterestMin=%s, totalInterestMax=%s, totalAdiCostMin=%s, totalAdiCostMax=%s, "
                + "totalCostMin=%s, totalCostMax=%s, "
                + "registrationFees=%s, landConservationFees=%s, notaryFees=%s, processingFees=%s, totalFees=%s, "
                + "marginHt=%s, vatOnMargin=%s, marginTtc=%s, monthlyPaymentTtc=%s, amortizationScheduleSize=%s]",
            amount, duration, cspCode, productType, nationalityCode, moroccanResident, age, totalMonthlyIncome,
            applicableRate, rateMin, rateMax,
            monthlyPayment, adiMonthlyPremium, allInMonthlyPayment, totalInterest, totalAdiCost, totalCost, taeg,
            monthlyPaymentMin, monthlyPaymentMax,
            adiMonthlyPremiumMin, adiMonthlyPremiumMax, allInMonthlyPaymentMin, allInMonthlyPaymentMax,
            totalInterestMin, totalInterestMax, totalAdiCostMin, totalAdiCostMax,
            totalCostMin, totalCostMax,
            registrationFees, landConservationFees, notaryFees, processingFees, totalFees,
            marginHt, vatOnMargin, marginTtc, monthlyPaymentTtc,
            amortizationSchedule == null ? null : amortizationSchedule.size());
    }
}