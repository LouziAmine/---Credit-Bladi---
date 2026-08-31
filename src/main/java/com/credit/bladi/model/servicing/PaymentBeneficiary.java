package com.credit.bladi.model.servicing;

import com.credit.bladi.model.vo.Rib;
import java.util.Objects;

public record PaymentBeneficiary(String name, Rib rib) {
    public PaymentBeneficiary {
        Objects.requireNonNull(name, "beneficiary name cannot be null");
        Objects.requireNonNull(rib, "beneficiary RIB cannot be null");
    }
}
