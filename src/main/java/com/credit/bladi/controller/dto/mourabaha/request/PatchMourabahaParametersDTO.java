package com.credit.bladi.controller.dto.mourabaha.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

@Schema(
    name = "PatchMourabahaParametersDTO",
    description = """
        Request payload to update Mourabaha Immobilier pricing parameters. Admin use only.

        Only provided fields are updated (partial update).
        The record to update is identified by the {id} path variable.
        """
)
public record PatchMourabahaParametersDTO(
    @Schema(description = "New annual profit rate (Taux HT) applied to the financed capital.", example = "5.25")
    @DecimalMin(value = "0.0", inclusive = false, message = "{validation.mourabahaParameters.profitRate.min}")
    @DecimalMax(value = "100", message = "{validation.mourabahaParameters.profitRate.max}")
    BigDecimal profitRate,

    @Schema(description = "New VAT rate applied to the profit margin (TVA sur marge). "
        + "E.g. 0.10 means 10%.", example = "0.10")
    @DecimalMin(value = "0.0", inclusive = false, message = "{validation.mourabahaParameters.vatRate.min}")
    @DecimalMax(value = "1.0", message = "{validation.mourabahaParameters.vatRate.max}")
    BigDecimal vatRate,

    @Schema(description = "New Takaful (Islamic cooperative insurance) monthly contribution rate, "
        + "applied to the financed capital and doubled when there is a co-borrower: same shape as "
        + "the Conventional path's ADI rate, priced separately here since Mourabaha has its own "
        + "bank-wide parameters. E.g. 0.0004 means 0.04% of capital per month.", example = "0.0004")
    @DecimalMin(value = "0.0", inclusive = false, message = "{validation.mourabahaParameters.takafulMonthlyRate.min}")
    @DecimalMax(value = "1.0", message = "{validation.mourabahaParameters.takafulMonthlyRate.max}")
    BigDecimal takafulMonthlyRate,

    @Schema(description = "Whether to set this record as the active configuration.", example = "true")
    Boolean active
) {}
