package com.credit.bladi.model.mourabaha;

import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.vo.Ratio;
import java.util.Objects;
import java.util.StringJoiner;

public class MourabahaParametersModel {

    private Long id;

    private Rate profitRate;

    private Ratio vatRate;

    private Boolean active;

    private MourabahaParametersModel() {
    }

    public Long getId() {
        return id;
    }

    public Rate getProfitRate() {
        return profitRate;
    }

    public Ratio getVatRate() {
        return vatRate;
    }

    public Boolean getActive() {
        return active;
    }

    private void setId(Long id) {
        this.id = id;
    }

    private void setProfitRate(Rate profitRate) {
        this.profitRate = profitRate;
    }

    private void setVatRate(Ratio vatRate) {
        this.vatRate = vatRate;
    }

    private void setActive(Boolean active) {
        this.active = active;
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    public static class Builder {

        private Long id;

        private Rate profitRate;

        private Ratio vatRate;

        private Boolean active;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder profitRate(Rate profitRate) {
            this.profitRate = profitRate;
            return this;
        }

        public Builder vatRate(Ratio vatRate) {
            this.vatRate = vatRate;
            return this;
        }

        public Builder active(Boolean active) {
            this.active = active;
            return this;
        }

        public MourabahaParametersModel build() {
            MourabahaParametersModel model = new MourabahaParametersModel();
            model.setId(id);
            model.setProfitRate(profitRate);
            model.setVatRate(vatRate);
            model.setActive(active);
            return model;
        }
    }

    public String describeChangesFrom(MourabahaParametersModel before) {
        StringJoiner changes = new StringJoiner(", ");
        appendIfChanged(changes, "profitRate", before.profitRate, profitRate);
        appendIfChanged(changes, "vatRate", before.vatRate, vatRate);
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
        if (before instanceof Ratio beforeRatio && after instanceof Ratio afterRatio) {
            return beforeRatio.equalsValue(afterRatio);
        }
        return Objects.equals(before, after);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MourabahaParametersModel other)) {
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
            "MourabahaParametersModel [id=%s, profitRate=%s, vatRate=%s, active=%s]",
            id, profitRate, vatRate, active);
    }
}
