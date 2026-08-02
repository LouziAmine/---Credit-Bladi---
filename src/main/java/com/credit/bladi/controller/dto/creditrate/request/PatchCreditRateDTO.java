package com.credit.bladi.controller.dto.creditrate.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Schema(
    name = "PatchCreditRateDTO",
    description = """
        Request payload to update a credit rate record. Admin use only.

        Only provided fields are updated (partial update).
        The record to update is identified by the {id} path variable.
        minDurationMonths and maxDurationMonths must be provided together — supplying only
        one of the two is rejected, since the pair forms a single duration range.
        """
)
public class PatchCreditRateDTO {

    @Schema(description = "New minimum interest rate (%).", example = "4.60")
    @DecimalMin(value = "0.0", inclusive = false, message = "rateMin must be strictly positive")
    @DecimalMax(value = "100.0", message = "rateMin exceeds sane maximum of 100%")
    private BigDecimal rateMin;

    @Schema(description = "New maximum interest rate (%).", example = "5.50")
    @DecimalMin(value = "0.0", inclusive = false, message = "rateMax must be strictly positive")
    @DecimalMax(value = "100.0", message = "rateMax exceeds sane maximum of 100%")
    private BigDecimal rateMax;

    @Schema(description = "New minimum loan duration, in months. Must be supplied together with "
        + "maxDurationMonths.", example = "12")
    @Min(value = 0, message = "minDurationMonths cannot be negative")
    private Integer minDurationMonths;

    @Schema(description = "New maximum loan duration, in months. Must be supplied together with "
        + "minDurationMonths.", example = "240")
    @Min(value = 0, message = "maxDurationMonths cannot be negative")
    private Integer maxDurationMonths;

    @Schema(description = "Whether to set this record as the active rate for its CSP code.", example = "true")
    private Boolean active;

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
        return o instanceof PatchCreditRateDTO && EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return String.format(
            "PatchCreditRateDTO [rateMin=%s, rateMax=%s, minDurationMonths=%s, maxDurationMonths=%s, active=%s]",
            rateMin, rateMax, minDurationMonths, maxDurationMonths, active);
    }
}
