package com.credit.bladi.controller.dto.simulation.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Schema(
    name = "AmortizationRowDTO",
    description = """
        One row of the month-by-month amortization schedule (tableau d'amortissement) at the \
        applicable rate/margin, for a constant monthly payment.

        Conventional loans populate `interest`; Mourabaha financings populate `marginHt` /
        `vatOnMargin` / `marginTtc` instead (the unused set is null, depending on productType).
        """
)
public class AmortizationRowDTO {

    @Schema(description = "Installment number (1-based).", example = "1")
    private Integer installmentNumber;

    @Schema(description = "Remaining principal balance at the start of this installment (MAD).",
        example = "500000.00")
    private BigDecimal beginningBalance;

    @Schema(description = "Conventional only — interest portion of this installment (MAD).",
        example = "2291.67")
    private BigDecimal interest;

    @Schema(description = "Mourabaha only — profit margin excluding VAT for this installment (Marge HT) (MAD).",
        example = "1050.00")
    private BigDecimal marginHt;

    @Schema(description = "Mourabaha only — VAT on the margin for this installment (TVA sur Marge) (MAD).",
        example = "105.00")
    private BigDecimal vatOnMargin;

    @Schema(description = "Mourabaha only — profit margin including VAT for this installment (Marge TTC) (MAD).",
        example = "1155.00")
    private BigDecimal marginTtc;

    @Schema(description = "Principal portion of this installment (MAD).", example = "1147.77")
    private BigDecimal principal;

    @Schema(description = "Total installment amount due (principal + interest/margin); may differ from the "
        + "nominal constant monthly payment by a few centimes on the final installment due to rounding "
        + "closure (MAD).", example = "3439.44")
    private BigDecimal payment;

    @Schema(description = "Remaining principal balance after this installment (0.00 on the final "
        + "installment) (MAD).", example = "498852.23")
    private BigDecimal endingBalance;

    public Integer getInstallmentNumber() {
        return installmentNumber;
    }

    public void setInstallmentNumber(Integer installmentNumber) {
        this.installmentNumber = installmentNumber;
    }

    public BigDecimal getBeginningBalance() {
        return beginningBalance;
    }

    public void setBeginningBalance(BigDecimal beginningBalance) {
        this.beginningBalance = beginningBalance;
    }

    public BigDecimal getInterest() {
        return interest;
    }

    public void setInterest(BigDecimal interest) {
        this.interest = interest;
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

    public BigDecimal getPrincipal() {
        return principal;
    }

    public void setPrincipal(BigDecimal principal) {
        this.principal = principal;
    }

    public BigDecimal getPayment() {
        return payment;
    }

    public void setPayment(BigDecimal payment) {
        this.payment = payment;
    }

    public BigDecimal getEndingBalance() {
        return endingBalance;
    }

    public void setEndingBalance(BigDecimal endingBalance) {
        this.endingBalance = endingBalance;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof AmortizationRowDTO && EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return String.format(
            "AmortizationRowDTO [installmentNumber=%s, beginningBalance=%s, interest=%s, marginHt=%s, "
                + "vatOnMargin=%s, marginTtc=%s, principal=%s, payment=%s, endingBalance=%s]",
            installmentNumber, beginningBalance, interest, marginHt, vatOnMargin, marginTtc, principal,
            payment, endingBalance);
    }
}
