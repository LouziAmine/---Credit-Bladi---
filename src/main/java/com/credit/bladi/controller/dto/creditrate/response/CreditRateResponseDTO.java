package com.credit.bladi.controller.dto.creditrate.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Schema(
    name = "CreditRateResponseDTO",
    description = "Credit rate record used to compute mortgage installments for a given CSP profile."
)
public class CreditRateResponseDTO {

    @Schema(description = "Record identifier.", example = "1")
    private Long id;

    @Schema(description = "CSP (socio-professional category) code this rate applies to.", example = "SALA")
    private String cspCode;

    @Schema(description = "Minimum interest rate (%).", example = "4.60")
    private BigDecimal rateMin;

    @Schema(description = "Maximum interest rate (%).", example = "5.50")
    private BigDecimal rateMax;

    @Schema(description = "Minimum loan duration, in months.", example = "12")
    private Integer minDurationMonths;

    @Schema(description = "Maximum loan duration, in months.", example = "240")
    private Integer maxDurationMonths;

    @Schema(description = "Whether this record is the active rate for its CSP code.", example = "true")
    private Boolean active;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCspCode() {
        return cspCode;
    }

    public void setCspCode(String cspCode) {
        this.cspCode = cspCode;
    }

    public BigDecimal getRateMin() {
        return rateMin;
    }

    public void setRateMin(BigDecimal rateMin) {
        this.rateMin = rateMin;
    }

    public BigDecimal getRateMax() {
        return rateMax;
    }

    public void setRateMax(BigDecimal rateMax) {
        this.rateMax = rateMax;
    }

    public Integer getMinDurationMonths() {
        return minDurationMonths;
    }

    public void setMinDurationMonths(Integer minDurationMonths) {
        this.minDurationMonths = minDurationMonths;
    }

    public Integer getMaxDurationMonths() {
        return maxDurationMonths;
    }

    public void setMaxDurationMonths(Integer maxDurationMonths) {
        this.maxDurationMonths = maxDurationMonths;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof CreditRateResponseDTO && EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return String.format(
            "CreditRateResponseDTO [id=%s, cspCode=%s, rateMin=%s, rateMax=%s, minDurationMonths=%s, "
                + "maxDurationMonths=%s, active=%s]",
            id, cspCode, rateMin, rateMax, minDurationMonths, maxDurationMonths, active);
    }
}
