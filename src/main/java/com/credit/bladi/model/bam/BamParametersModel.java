package com.credit.bladi.model.bam;

import static com.credit.bladi.exception.BAMException.BAMExceptionType.AGE_LIMIT_EXCEEDED;
import static com.credit.bladi.exception.BAMException.BAMExceptionType.DEBT_RATIO_EXCEEDED;
import static com.credit.bladi.exception.BAMException.BAMExceptionType.DEPOSIT_INSUFFICIENT;
import static com.credit.bladi.exception.BamParametersException.BamParametersExceptionType.INVALID_PROCESSING_FEES_RANGE;

import com.credit.bladi.exception.BAMException;
import com.credit.bladi.exception.BamParametersException;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Ratio;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.StringJoiner;

public class BamParametersModel {

    private Long id;

    private Ratio processingFeesRatio;

    private Money minProcessingFees;

    private Money maxProcessingFees;

    private Ratio registrationFeeRatio;

    private Ratio landConservationRatio;

    private Ratio notaryFeeRatio;

    private Ratio ltvRatio;

    private Ratio maxDebtRatio;

    private Integer maxAgeAtMaturity;

    private Ratio adiMonthlyRate;

    private Boolean active;

    private BamParametersModel() {
    }

    public Long getId() {
        return id;
    }

    public Ratio getProcessingFeesRatio() {
        return processingFeesRatio;
    }

    public Money getMinProcessingFees() {
        return minProcessingFees;
    }

    public Money getMaxProcessingFees() {
        return maxProcessingFees;
    }

    public Ratio getRegistrationFeeRatio() {
        return registrationFeeRatio;
    }

    public Ratio getLandConservationRatio() {
        return landConservationRatio;
    }

    public Ratio getNotaryFeeRatio() {
        return notaryFeeRatio;
    }

    public Ratio getLtvRatio() {
        return ltvRatio;
    }

    public Ratio getMaxDebtRatio() {
        return maxDebtRatio;
    }

    public Integer getMaxAgeAtMaturity() {
        return maxAgeAtMaturity;
    }

    public Ratio getAdiMonthlyRate() {
        return adiMonthlyRate;
    }

    public Boolean getActive() {
        return active;
    }

    public void assertWithinLtv(Money loanAmount, Money propertyValue) {
        Money maxLoan =
            Money.of(propertyValue.amount().multiply(ltvRatio.value()).setScale(2, RoundingMode.HALF_UP));
        if (loanAmount.isGreaterThan(maxLoan)) {
            throw BAMException.of(DEPOSIT_INSUFFICIENT, ltvRatio.value());
        }
    }

    public void assertWithinDebtRatio(Money allInMonthlyPayment, Money totalMonthlyIncome) {
        Money maxMonthlyPayment = Money.of(
            totalMonthlyIncome.amount().multiply(maxDebtRatio.value()).setScale(2, RoundingMode.HALF_UP));
        if (allInMonthlyPayment.isGreaterThan(maxMonthlyPayment)) {
            throw BAMException.of(DEBT_RATIO_EXCEEDED, maxDebtRatio.value());
        }
    }

    public void assertEligibleAge(int currentAge, int durationMonths) {
        int ageAtMaturity = currentAge + (int) Math.ceil(durationMonths / 12.0);
        if (ageAtMaturity > maxAgeAtMaturity) {
            throw BAMException.of(AGE_LIMIT_EXCEEDED);
        }
    }

    public String describeChangesFrom(BamParametersModel before) {
        StringJoiner changes = new StringJoiner(", ");
        appendIfChanged(changes, "processingFeesRatio", before.processingFeesRatio, processingFeesRatio);
        appendIfChanged(changes, "minProcessingFees", before.minProcessingFees, minProcessingFees);
        appendIfChanged(changes, "maxProcessingFees", before.maxProcessingFees, maxProcessingFees);
        appendIfChanged(changes, "registrationFeeRatio", before.registrationFeeRatio, registrationFeeRatio);
        appendIfChanged(changes, "landConservationRatio", before.landConservationRatio, landConservationRatio);
        appendIfChanged(changes, "notaryFeeRatio", before.notaryFeeRatio, notaryFeeRatio);
        appendIfChanged(changes, "ltvRatio", before.ltvRatio, ltvRatio);
        appendIfChanged(changes, "maxDebtRatio", before.maxDebtRatio, maxDebtRatio);
        appendIfChanged(changes, "maxAgeAtMaturity", before.maxAgeAtMaturity, maxAgeAtMaturity);
        appendIfChanged(changes, "adiMonthlyRate", before.adiMonthlyRate, adiMonthlyRate);
        appendIfChanged(changes, "active", before.active, active);
        return changes.toString();
    }

    private static void appendIfChanged(StringJoiner changes, String field, Object before, Object after) {
        if (!valuesEqual(before, after)) {
            changes.add(field + ": " + before + " -> " + after);
        }
    }

    private static boolean valuesEqual(Object before, Object after) {
        if (before instanceof BigDecimal beforeValue && after instanceof BigDecimal afterValue) {
            return beforeValue.compareTo(afterValue) == 0;
        }
        if (before instanceof Money beforeMoney && after instanceof Money afterMoney) {
            return beforeMoney.equalsValue(afterMoney);
        }
        if (before instanceof Ratio beforeRatio && after instanceof Ratio afterRatio) {
            return beforeRatio.equalsValue(afterRatio);
        }
        return Objects.equals(before, after);
    }

    private void setId(Long id) {
        this.id = id;
    }

    private void setProcessingFeesRatio(Ratio processingFeesRatio) {
        this.processingFeesRatio = processingFeesRatio;
    }

    private void setMinProcessingFees(Money minProcessingFees) {
        this.minProcessingFees = minProcessingFees;
    }

    private void setMaxProcessingFees(Money maxProcessingFees) {
        this.maxProcessingFees = maxProcessingFees;
    }

    private void setRegistrationFeeRatio(Ratio registrationFeeRatio) {
        this.registrationFeeRatio = registrationFeeRatio;
    }

    private void setLandConservationRatio(Ratio landConservationRatio) {
        this.landConservationRatio = landConservationRatio;
    }

    private void setNotaryFeeRatio(Ratio notaryFeeRatio) {
        this.notaryFeeRatio = notaryFeeRatio;
    }

    private void setLtvRatio(Ratio ltvRatio) {
        this.ltvRatio = ltvRatio;
    }

    private void setMaxDebtRatio(Ratio maxDebtRatio) {
        this.maxDebtRatio = maxDebtRatio;
    }

    private void setMaxAgeAtMaturity(Integer maxAgeAtMaturity) {
        this.maxAgeAtMaturity = maxAgeAtMaturity;
    }

    private void setAdiMonthlyRate(Ratio adiMonthlyRate) {
        this.adiMonthlyRate = adiMonthlyRate;
    }

    private void setActive(Boolean active) {
        this.active = active;
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    public static class Builder {

        private Long id;

        private Ratio processingFeesRatio;

        private Money minProcessingFees;

        private Money maxProcessingFees;

        private Ratio registrationFeeRatio;

        private Ratio landConservationRatio;

        private Ratio notaryFeeRatio;

        private Ratio ltvRatio;

        private Ratio maxDebtRatio;

        private Integer maxAgeAtMaturity;

        private Ratio adiMonthlyRate;

        private Boolean active;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder processingFeesRatio(Ratio processingFeesRatio) {
            this.processingFeesRatio = processingFeesRatio;
            return this;
        }

        public Builder minProcessingFees(Money minProcessingFees) {
            this.minProcessingFees = minProcessingFees;
            return this;
        }

        public Builder maxProcessingFees(Money maxProcessingFees) {
            this.maxProcessingFees = maxProcessingFees;
            return this;
        }

        public Builder registrationFeeRatio(Ratio registrationFeeRatio) {
            this.registrationFeeRatio = registrationFeeRatio;
            return this;
        }

        public Builder landConservationRatio(Ratio landConservationRatio) {
            this.landConservationRatio = landConservationRatio;
            return this;
        }

        public Builder notaryFeeRatio(Ratio notaryFeeRatio) {
            this.notaryFeeRatio = notaryFeeRatio;
            return this;
        }

        public Builder ltvRatio(Ratio ltvRatio) {
            this.ltvRatio = ltvRatio;
            return this;
        }

        public Builder maxDebtRatio(Ratio maxDebtRatio) {
            this.maxDebtRatio = maxDebtRatio;
            return this;
        }

        public Builder maxAgeAtMaturity(Integer maxAgeAtMaturity) {
            this.maxAgeAtMaturity = maxAgeAtMaturity;
            return this;
        }

        public Builder adiMonthlyRate(Ratio adiMonthlyRate) {
            this.adiMonthlyRate = adiMonthlyRate;
            return this;
        }

        public Builder active(Boolean active) {
            this.active = active;
            return this;
        }

        public BamParametersModel build() {
            validateRatio("processingFeesRatio", processingFeesRatio, BigDecimal.valueOf(0.01));
            validateRatio("registrationFeeRatio", registrationFeeRatio, BigDecimal.valueOf(0.05));
            validateRatio("landConservationRatio", landConservationRatio, BigDecimal.valueOf(0.05));
            validateRatio("notaryFeeRatio", notaryFeeRatio, BigDecimal.valueOf(0.05));
            validateRatio("ltvRatio", ltvRatio, BigDecimal.valueOf(0.90));
            validateRatio("maxDebtRatio", maxDebtRatio, BigDecimal.valueOf(0.40));
            validateRatio("adiMonthlyRate", adiMonthlyRate, BigDecimal.valueOf(0.001));
            validateAgeCeiling(maxAgeAtMaturity, 85);
            validateProcessingFeesRange(minProcessingFees, maxProcessingFees);

            BamParametersModel model = new BamParametersModel();
            model.setId(id);
            model.setProcessingFeesRatio(processingFeesRatio);
            model.setMinProcessingFees(minProcessingFees);
            model.setMaxProcessingFees(maxProcessingFees);
            model.setRegistrationFeeRatio(registrationFeeRatio);
            model.setLandConservationRatio(landConservationRatio);
            model.setNotaryFeeRatio(notaryFeeRatio);
            model.setLtvRatio(ltvRatio);
            model.setMaxDebtRatio(maxDebtRatio);
            model.setMaxAgeAtMaturity(maxAgeAtMaturity);
            model.setAdiMonthlyRate(adiMonthlyRate);
            model.setActive(active);
            return model;
        }

        private static void validateRatio(String fieldName, Ratio value, BigDecimal max) {
            if (value == null) {
                return;
            }
            if (value.value().compareTo(max) > 0) {
                throw new IllegalArgumentException(
                    fieldName + " must be strictly positive and at most " + max + " (got " + value.value() + ")");
            }
        }

        private static void validateAgeCeiling(Integer value, int max) {
            if (value == null) {
                return;
            }
            if (value < 1 || value > max) {
                throw new IllegalArgumentException(
                    "maxAgeAtMaturity must be between 1 and " + max + " (got " + value + ")");
            }
        }

        private static void validateProcessingFeesRange(Money min, Money max) {
            if (min != null && max != null && min.isGreaterThan(max)) {
                throw BamParametersException.of(INVALID_PROCESSING_FEES_RANGE);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BamParametersModel other)) {
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
            "BamParametersModel [id=%s, processingFeesRatio=%s, minProcessingFees=%s, maxProcessingFees=%s, "
                + "registrationFeeRatio=%s, landConservationRatio=%s, notaryFeeRatio=%s, "
                + "ltvRatio=%s, maxDebtRatio=%s, maxAgeAtMaturity=%s, adiMonthlyRate=%s, active=%s]",
            id, processingFeesRatio, minProcessingFees, maxProcessingFees,
            registrationFeeRatio, landConservationRatio, notaryFeeRatio,
            ltvRatio, maxDebtRatio, maxAgeAtMaturity, adiMonthlyRate, active);
    }
}