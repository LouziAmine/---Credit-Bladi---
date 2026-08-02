package com.credit.bladi.controller.dto.mourabaha.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Schema(
    name = "PatchMourabahaParametersDTO",
    description = """
        Request payload to update Mourabaha Immobilier pricing parameters. Admin use only.

        Only provided fields are updated (partial update).
        The record to update is identified by the {id} path variable.
        """
)
public class PatchMourabahaParametersDTO {

    @Schema(description = "New annual profit rate (Taux HT) applied to the financed capital.", example = "5.25")
    @DecimalMin(value = "0.0", inclusive = false, message = "profitRate must be strictly positive")
    @DecimalMax(value = "100", message = "profitRate exceeds sane maximum of 100%")
    private BigDecimal profitRate;

    @Schema(description = "New VAT rate applied to the profit margin (TVA sur marge). "
        + "E.g. 0.10 means 10%.", example = "0.10")
    @DecimalMin(value = "0.0", inclusive = false, message = "vatRate must be strictly positive")
    @DecimalMax(value = "1.0", message = "vatRate cannot exceed 1 (100%)")
    private BigDecimal vatRate;

    @Schema(description = "Whether to set this record as the active configuration.", example = "true")
    private Boolean active;

    public BigDecimal getProfitRate() {
        return profitRate;
    }

    public void setProfitRate(BigDecimal profitRate) {
        this.profitRate = profitRate;
    }

    public BigDecimal getVatRate() {
        return vatRate;
    }

    public void setVatRate(BigDecimal vatRate) {
        this.vatRate = vatRate;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof PatchMourabahaParametersDTO && EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return String.format(
            "PatchMourabahaParametersDTO [profitRate=%s, vatRate=%s, active=%s]",
            profitRate, vatRate, active);
    }
}
