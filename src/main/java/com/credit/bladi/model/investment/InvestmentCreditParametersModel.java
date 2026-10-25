package com.credit.bladi.model.investment;

import static com.credit.bladi.exception.InvestmentCreditException.InvestmentCreditExceptionType.COMPANY_AGE_INSUFFICIENT;
import static com.credit.bladi.exception.InvestmentCreditException.InvestmentCreditExceptionType.DEBT_SERVICE_RATIO_EXCEEDED;
import static com.credit.bladi.exception.InvestmentCreditException.InvestmentCreditExceptionType.FINANCING_RATIO_EXCEEDED;
import static com.credit.bladi.exception.InvestmentCreditException.InvestmentCreditExceptionType.GUARANTEE_COVERAGE_INSUFFICIENT;

import com.credit.bladi.exception.InvestmentCreditException;
import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.model.enums.RateFormula;
import com.credit.bladi.model.parameters.VersionedParametersModel;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.vo.Ratio;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.UUID;

public final class InvestmentCreditParametersModel
        implements VersionedParametersModel<InvestmentCreditParametersModel> {
    private final Long id;

    private final UUID publicId;

    private final EnterpriseType enterpriseType;

    private final Rate rateMin;

    private final Rate rateMax;

    private final RateFormula rateFormula;

    private final Integer minCompanyAgeYears;

    private final Ratio maxFinancingToRevenueRatio;

    private final Ratio minGuaranteeCoverageRatio;

    private final Ratio maxDebtServiceRatio;

    private final Ratio processingFeesRatio;

    private final Ratio insurancePremiumRatio;

    private final Boolean active;

    private InvestmentCreditParametersModel(Builder builder) {
        this.id = builder.id;
        this.publicId = builder.publicId;
        this.enterpriseType = builder.enterpriseType;
        this.rateMin = builder.rateMin;
        this.rateMax = builder.rateMax;
        this.rateFormula = builder.rateFormula;
        this.minCompanyAgeYears = builder.minCompanyAgeYears;
        this.maxFinancingToRevenueRatio = builder.maxFinancingToRevenueRatio;
        this.minGuaranteeCoverageRatio = builder.minGuaranteeCoverageRatio;
        this.maxDebtServiceRatio = builder.maxDebtServiceRatio;
        this.processingFeesRatio = builder.processingFeesRatio;
        this.insurancePremiumRatio = builder.insurancePremiumRatio;
        this.active = builder.active;
    }

    public Long getId() {
        return id;
    }

    public UUID getPublicId() {
        return publicId;
    }

    public EnterpriseType getEnterpriseType() {
        return enterpriseType;
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

    public Integer getMinCompanyAgeYears() {
        return minCompanyAgeYears;
    }

    public Ratio getMaxFinancingToRevenueRatio() {
        return maxFinancingToRevenueRatio;
    }

    public Ratio getMinGuaranteeCoverageRatio() {
        return minGuaranteeCoverageRatio;
    }

    public Ratio getMaxDebtServiceRatio() {
        return maxDebtServiceRatio;
    }

    public Ratio getProcessingFeesRatio() {
        return processingFeesRatio;
    }

    public Ratio getInsurancePremiumRatio() {
        return insurancePremiumRatio;
    }

    public Boolean getActive() {
        return active;
    }

    public void assertMinCompanyAge(int companyAgeYears) {
        if (companyAgeYears < minCompanyAgeYears) {
            throw InvestmentCreditException.of(COMPANY_AGE_INSUFFICIENT, minCompanyAgeYears);
        }
    }

    public void assertWithinFinancingRatio(Money amount, Money annualRevenue) {
        Money maxAmount = Money.of(annualRevenue.amount()
            .multiply(maxFinancingToRevenueRatio.value())
            .setScale(2, RoundingMode.HALF_UP));
        if (amount.isGreaterThan(maxAmount)) {
            throw InvestmentCreditException.of(FINANCING_RATIO_EXCEEDED, maxFinancingToRevenueRatio.value());
        }
    }

    public void assertGuaranteeCoverage(Money guaranteesValue, Money amount) {
        Money minGuaranteesValue = Money.of(amount.amount()
            .multiply(minGuaranteeCoverageRatio.value())
            .setScale(2, RoundingMode.HALF_UP));
        if (guaranteesValue.isLessThan(minGuaranteesValue)) {
            throw InvestmentCreditException.of(GUARANTEE_COVERAGE_INSUFFICIENT, minGuaranteeCoverageRatio.value());
        }
    }

    public void assertWithinDebtServiceRatio(Money totalAnnualDebtService, Money netAnnualResult) {
        Money maxDebtService = Money.of(netAnnualResult.amount()
            .multiply(maxDebtServiceRatio.value())
            .setScale(2, RoundingMode.HALF_UP));
        if (totalAnnualDebtService.isGreaterThan(maxDebtService)) {
            throw InvestmentCreditException.of(DEBT_SERVICE_RATIO_EXCEEDED, maxDebtServiceRatio.value());
        }
    }

    public String describeChangesFrom(InvestmentCreditParametersModel before) {
        StringJoiner changes = new StringJoiner(", ");
        appendIfChanged(changes, "rateMin", before.rateMin, rateMin);
        appendIfChanged(changes, "rateMax", before.rateMax, rateMax);
        appendIfChanged(changes, "rateFormula", before.rateFormula, rateFormula);
        appendIfChanged(changes, "minCompanyAgeYears", before.minCompanyAgeYears, minCompanyAgeYears);
        appendIfChanged(changes, "maxFinancingToRevenueRatio",
            before.maxFinancingToRevenueRatio, maxFinancingToRevenueRatio);
        appendIfChanged(changes, "minGuaranteeCoverageRatio",
            before.minGuaranteeCoverageRatio, minGuaranteeCoverageRatio);
        appendIfChanged(changes, "maxDebtServiceRatio", before.maxDebtServiceRatio, maxDebtServiceRatio);
        appendIfChanged(changes, "processingFeesRatio", before.processingFeesRatio, processingFeesRatio);
        appendIfChanged(changes, "insurancePremiumRatio", before.insurancePremiumRatio, insurancePremiumRatio);
        appendIfChanged(changes, "active", before.active, active);
        return changes.toString();
    }

    private static void appendIfChanged(StringJoiner changes, String field, Object before, Object after) {
        if (!Objects.equals(before, after)) {
            changes.add(field + ": " + before + " -> " + after);
        }
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;

        private UUID publicId;

        private EnterpriseType enterpriseType;

        private Rate rateMin;

        private Rate rateMax;

        private RateFormula rateFormula;

        private Integer minCompanyAgeYears;

        private Ratio maxFinancingToRevenueRatio;

        private Ratio minGuaranteeCoverageRatio;

        private Ratio maxDebtServiceRatio;

        private Ratio processingFeesRatio;

        private Ratio insurancePremiumRatio;

        private Boolean active;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder publicId(UUID publicId) {
            this.publicId = publicId;
            return this;
        }

        public Builder enterpriseType(EnterpriseType enterpriseType) {
            this.enterpriseType = enterpriseType;
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

        public Builder minCompanyAgeYears(Integer minCompanyAgeYears) {
            this.minCompanyAgeYears = minCompanyAgeYears;
            return this;
        }

        public Builder maxFinancingToRevenueRatio(Ratio maxFinancingToRevenueRatio) {
            this.maxFinancingToRevenueRatio = maxFinancingToRevenueRatio;
            return this;
        }

        public Builder minGuaranteeCoverageRatio(Ratio minGuaranteeCoverageRatio) {
            this.minGuaranteeCoverageRatio = minGuaranteeCoverageRatio;
            return this;
        }

        public Builder maxDebtServiceRatio(Ratio maxDebtServiceRatio) {
            this.maxDebtServiceRatio = maxDebtServiceRatio;
            return this;
        }

        public Builder processingFeesRatio(Ratio processingFeesRatio) {
            this.processingFeesRatio = processingFeesRatio;
            return this;
        }

        public Builder insurancePremiumRatio(Ratio insurancePremiumRatio) {
            this.insurancePremiumRatio = insurancePremiumRatio;
            return this;
        }

        public Builder active(Boolean active) {
            this.active = active;
            return this;
        }

        public InvestmentCreditParametersModel build() {
            validateRateOrder(rateMin, rateMax);

            return new InvestmentCreditParametersModel(this);
        }

        private static void validateRateOrder(Rate rateMin, Rate rateMax) {
            if (rateMin != null && rateMax != null && rateMin.isGreaterThan(rateMax)) {
                throw new IllegalArgumentException(
                    "rateMin (" + rateMin.value() + ") must not exceed rateMax (" + rateMax.value() + ")");
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InvestmentCreditParametersModel other)) {
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
            "InvestmentCreditParametersModel [id=%s, enterpriseType=%s, rateMin=%s, rateMax=%s, rateFormula=%s, "
                + "minCompanyAgeYears=%s, maxFinancingToRevenueRatio=%s, minGuaranteeCoverageRatio=%s, "
                + "maxDebtServiceRatio=%s, processingFeesRatio=%s, insurancePremiumRatio=%s, active=%s]",
            id, enterpriseType, rateMin, rateMax, rateFormula, minCompanyAgeYears, maxFinancingToRevenueRatio,
            minGuaranteeCoverageRatio, maxDebtServiceRatio, processingFeesRatio, insurancePremiumRatio, active);
    }
}
