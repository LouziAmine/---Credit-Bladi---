package com.credit.bladi.model.parameters;

import com.credit.bladi.exception.RegulatoryLimitException;
import com.credit.bladi.model.vo.Rate;
import java.math.BigDecimal;
import java.util.Objects;

public record RegulatoryLimits(Rate maxAnnualRate, RateBasis simulationBasis) {
    public RegulatoryLimits {
        Objects.requireNonNull(maxAnnualRate, "maxAnnualRate cannot be null");
        Objects.requireNonNull(simulationBasis, "simulationBasis cannot be null");
    }

    public RegulatoryLimits(Rate maxAnnualRate) {
        this(maxAnnualRate, RateBasis.NOMINAL);
    }

    public boolean exceedsMaxRate(Rate rate) {
        return rate.isGreaterThan(maxAnnualRate);
    }

    public void assertWithinMaxRate(Rate rate) {
        if (exceedsMaxRate(rate)) {
            throw RegulatoryLimitException.rateAboveLegalMaximum(rate.value().toPlainString(),
                maxAnnualRate.value().toPlainString());
        }
    }

    public void assertSimulationWithinMaxRate(Rate nominalRate, BigDecimal tegPercent) {
        BigDecimal compared = simulationBasis == RateBasis.TEG && tegPercent != null
            ? tegPercent
            : nominalRate.value();
        if (compared.compareTo(maxAnnualRate.value()) > 0) {
            throw RegulatoryLimitException.rateAboveLegalMaximum(compared.toPlainString(),
                maxAnnualRate.value().toPlainString());
        }
    }
}
