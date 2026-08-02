package com.credit.bladi.controller.dto.mourabaha.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Schema(
    name = "MourabahaParametersResponseDTO",
    description = "Mourabaha Immobilier pricing parameters record (profit rate + VAT on margin)."
)
public class MourabahaParametersResponseDTO {

    @Schema(description = "Record identifier.", example = "1")
    private Long id;

    @Schema(description = "Annual profit rate (Taux HT) applied to the financed capital.", example = "5.25")
    private BigDecimal profitRate;

    @Schema(description = "VAT rate applied to the profit margin (TVA sur marge).", example = "0.10")
    private BigDecimal vatRate;

    @Schema(description = "Whether this record is the active configuration.", example = "true")
    private Boolean active;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
        return o instanceof MourabahaParametersResponseDTO && EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return String.format(
            "MourabahaParametersResponseDTO [id=%s, profitRate=%s, vatRate=%s, active=%s]",
            id, profitRate, vatRate, active);
    }
}
