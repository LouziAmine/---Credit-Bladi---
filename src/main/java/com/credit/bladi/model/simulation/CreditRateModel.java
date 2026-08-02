package com.credit.bladi.model.simulation;

import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.vo.DurationRange;
import com.credit.bladi.model.vo.Rate;
import java.util.Objects;
import java.util.StringJoiner;

public class CreditRateModel {

    private Long id;

    private CspCode cspCode;

    private Rate rateMin;

    private Rate rateMax;

    private DurationRange durationRange;

    private Boolean active;

    private CreditRateModel() {
    }

    public Long getId() {
        return id;
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

    public DurationRange getDurationRange() {
        return durationRange;
    }

    public Boolean getActive() {
        return active;
    }

    private void setId(Long id) {
        this.id = id;
    }

    private void setCspCode(CspCode cspCode) {
        this.cspCode = cspCode;
    }

    private void setRateMin(Rate rateMin) {
        this.rateMin = rateMin;
    }

    private void setRateMax(Rate rateMax) {
        this.rateMax = rateMax;
    }

    private void setDurationRange(DurationRange durationRange) {
        this.durationRange = durationRange;
    }

    private void setActive(Boolean active) {
        this.active = active;
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    public static class Builder {

        private Long id;

        private CspCode cspCode;

        private Rate rateMin;

        private Rate rateMax;

        private DurationRange durationRange;

        private Boolean active;

        public Builder id(Long id) {
            this.id = id;
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

            CreditRateModel model = new CreditRateModel();
            model.setId(id);
            model.setCspCode(cspCode);
            model.setRateMin(rateMin);
            model.setRateMax(rateMax);
            model.setDurationRange(durationRange);
            model.setActive(active);
            return model;
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
        appendIfChanged(changes, "durationRange", before.durationRange, durationRange);
        appendIfChanged(changes, "active", before.active, active);
        return changes.toString();
    }

    private static void appendIfChanged(StringJoiner changes, String field, Object before, Object after) {
        if (!valuesEqual(before, after)) {
            changes.add(field + ": " + before + " -> " + after);
        }
    }

    private static boolean valuesEqual(Object before, Object after) {
        if (before instanceof Rate beforeRate && after instanceof Rate afterRate) {
            return beforeRate.equalsValue(afterRate);
        }
        return Objects.equals(before, after);
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
            "CreditRateModel [id=%s, cspCode=%s, rateMin=%s, rateMax=%s, durationRange=%s, active=%s]",
            id, cspCode, rateMin, rateMax, durationRange, active);
    }
}