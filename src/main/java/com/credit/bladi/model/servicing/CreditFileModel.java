package com.credit.bladi.model.servicing;

import com.credit.bladi.model.enums.CreditFileStatus;
import com.credit.bladi.model.vo.Money;
import java.time.Instant;
import java.util.UUID;

public record CreditFileModel(
        Long id,
        UUID publicId,
        Long demandId,
        String reference,
        Instant disbursedAt,
        Money disbursedAmount,
        CreditFileStatus status,

        String managedByUsername
) {
    public boolean isActive() {
        return status == CreditFileStatus.ACTIVE;
    }
}
