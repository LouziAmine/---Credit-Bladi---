package com.credit.bladi.model.mourabaha;

import com.credit.bladi.model.parameters.VersionedParametersModel;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.vo.Ratio;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;

public final class MourabahaParametersModel implements VersionedParametersModel<MourabahaParametersModel> {
    private final Long id;

    private final UUID publicId;

    private final Rate profitRate;

    private final Ratio vatRate;

    private final Rate takafulMonthlyRate;

    private final Boolean active;

    private MourabahaParametersModel(Builder builder) {
        this.id = builder.id;
        this.publicId = builder.publicId;
        this.profitRate = builder.profitRate;
        this.vatRate = builder.vatRate;
        this.takafulMonthlyRate = builder.takafulMonthlyRate;
        this.active = builder.active;
    }

    public Long getId() {
        return id;
    }

    public UUID getPublicId() {
        return publicId;
    }

    public Rate getProfitRate() {
        return profitRate;
    }

    public Ratio getVatRate() {
        return vatRate;
    }

    public Rate getTakafulMonthlyRate() {
        return takafulMonthlyRate;
    }

    public Boolean getActive() {
        return active;
    }

    public MourabahaParametersModel withProfitRate(Rate agreedProfitRate) {
        return getBuilder().id(id).publicId(publicId).profitRate(agreedProfitRate).vatRate(vatRate)
            .takafulMonthlyRate(takafulMonthlyRate).active(active).build();
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;

        private UUID publicId;

        private Rate profitRate;

        private Ratio vatRate;

        private Rate takafulMonthlyRate;

        private Boolean active;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder publicId(UUID publicId) {
            this.publicId = publicId;
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

        public Builder takafulMonthlyRate(Rate takafulMonthlyRate) {
            this.takafulMonthlyRate = takafulMonthlyRate;
            return this;
        }

        public Builder active(Boolean active) {
            this.active = active;
            return this;
        }

        public MourabahaParametersModel build() {
            return new MourabahaParametersModel(this);
        }
    }

    public String describeChangesFrom(MourabahaParametersModel before) {
        StringJoiner changes = new StringJoiner(", ");
        appendIfChanged(changes, "profitRate", before.profitRate, profitRate);
        appendIfChanged(changes, "vatRate", before.vatRate, vatRate);
        appendIfChanged(changes, "takafulMonthlyRate", before.takafulMonthlyRate, takafulMonthlyRate);
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
            "MourabahaParametersModel [id=%s, profitRate=%s, vatRate=%s, takafulMonthlyRate=%s, active=%s]",
            id, profitRate, vatRate, takafulMonthlyRate, active);
    }
}
