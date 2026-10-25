package com.credit.bladi.model.creditrate;

import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.RateFormula;
import com.credit.bladi.model.parameters.VersionedParametersModel;
import com.credit.bladi.model.vo.DurationRange;
import com.credit.bladi.model.vo.Rate;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.UUID;

public final class CreditRateModel implements VersionedParametersModel<CreditRateModel> {
    private final Long id;

    private final UUID publicId;

    private final CspCode cspCode;

    private final Rate rateMin;

    private final Rate rateMax;

    private final RateFormula rateFormula;

    private final DurationRange durationRange;

    private final Boolean active;

    private CreditRateModel(Builder builder) {
        this.id = builder.id;
        this.publicId = builder.publicId;
        this.cspCode = builder.cspCode;
        this.rateMin = builder.rateMin;
        this.rateMax = builder.rateMax;
        this.rateFormula = builder.rateFormula;
        this.durationRange = builder.durationRange;
        this.active = builder.active;
    }

    public Long getId() {
        return id;
    }

    public UUID getPublicId() {
        return publicId;
    }

    public CspCode getCspCode() {
        return cspCode;
    }

    public Rate getRateMin() {
        return rateMin;
    }

    public Rate getRateMax() {
        return rateMax;
    }

    @Override
    public Optional<Rate> highestAnnualRate() {
        return Optional.ofNullable(rateMax);
    }

    public RateFormula getRateFormula() {
        return rateFormula;
    }

    public Rate calculateRate() {
        return rateFormula.apply(rateMin, rateMax);
    }

    public DurationRange getDurationRange() {
        return durationRange;
    }

    public Boolean getActive() {
        return active;
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;

        private UUID publicId;

        private CspCode cspCode;

        private Rate rateMin;

        private Rate rateMax;

        private RateFormula rateFormula;

        private DurationRange durationRange;

        private Boolean active;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder publicId(UUID publicId) {
            this.publicId = publicId;
            return this;
        }

        public Builder cspCode(CspCode cspCode) {
            this.cspCode = cspCode;
            return this;
        }

        public Builder rateMin(Rate rateMin) {
            this.rateMin = rateMin;
            return this;
        }

        public Builder rateMax(Rate rateMax) {
            this.rateMax = rateMax;
            return this;
        }

        public Builder rateFormula(RateFormula rateFormula) {
            this.rateFormula = rateFormula;
            return this;
        }

        public Builder durationRange(DurationRange durationRange) {
            this.durationRange = durationRange;
            return this;
        }

        public Builder active(Boolean active) {
            this.active = active;
            return this;
        }

        public CreditRateModel build() {
            validateRateOrder(rateMin, rateMax);

            return new CreditRateModel(this);
        }

        private static void validateRateOrder(Rate rateMin, Rate rateMax) {
            if (rateMin != null && rateMax != null && rateMin.isGreaterThan(rateMax)) {
                throw new IllegalArgumentException(
                    "rateMin (" + rateMin.value() + ") must not exceed rateMax (" + rateMax.value() + ")");
            }
        }
    }

    public String describeChangesFrom(CreditRateModel before) {
        StringJoiner changes = new StringJoiner(", ");
        appendIfChanged(changes, "rateMin", before.rateMin, rateMin);
        appendIfChanged(changes, "rateMax", before.rateMax, rateMax);
        appendIfChanged(changes, "rateFormula", before.rateFormula, rateFormula);
        appendIfChanged(changes, "durationRange", before.durationRange, durationRange);
        appendIfChanged(changes, "active", before.active, active);
        return changes.toString();
    }

    private static void appendIfChanged(StringJoiner changes, String field, Object before, Object after) {
        if (!Objects.equals(before, after)) {
            changes.add(field + ": " + before + " -> " + after);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CreditRateModel other)) {
            return false;
        }
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return String.format(
            "CreditRateModel [id=%s, cspCode=%s, rateMin=%s, rateMax=%s, rateFormula=%s, durationRange=%s, "
                + "active=%s]",
            id, cspCode, rateMin, rateMax, rateFormula, durationRange, active);
    }
}
