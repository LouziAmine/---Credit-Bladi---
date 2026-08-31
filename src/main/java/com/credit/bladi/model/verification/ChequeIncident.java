package com.credit.bladi.model.verification;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ChequeIncident(
    String type,
    LocalDate date,
    BigDecimal amount,
    boolean resolved
) {
}
