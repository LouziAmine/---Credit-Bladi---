package com.credit.bladi.controller.dto.mourabaha.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.UUID;

@Schema(
    name = "MourabahaParametersResponseDTO",
    description = "Mourabaha Immobilier pricing parameters record (profit rate + VAT on margin)."
)
public record MourabahaParametersResponseDTO(
    @Schema(description = "Record identifier.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b") UUID id,
    @Schema(description = "Annual profit rate (Taux HT) applied to the financed capital.", example = "5.25")
    BigDecimal profitRate,
    @Schema(description = "VAT rate applied to the profit margin (TVA sur marge).", example = "0.10")
    BigDecimal vatRate,
    @Schema(description = "Takaful (Islamic cooperative insurance) monthly contribution rate applied to the "
        + "financed capital, doubled when there is a co-borrower.", example = "0.0004")
    BigDecimal takafulMonthlyRate,
    @Schema(description = "Whether this record is the active configuration.", example = "true") Boolean active
) {}
