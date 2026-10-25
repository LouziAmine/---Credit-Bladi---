package com.credit.bladi.model.mortgage.response;

import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.model.mortgage.FinancialSnapshot;
import com.credit.bladi.model.mortgage.RateBoundsSnapshot;
import com.credit.bladi.model.mourabaha.MourabahaAmortizationRow;
import com.credit.bladi.model.mourabaha.MourabahaFinancialSnapshot;
import com.credit.bladi.model.vo.AmortizationRow;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import java.math.BigDecimal;
import java.util.List;

public record MortgageCreditResponse(
        Money amount,
        Integer duration,
        ProductType productType,
        CspCode cspCode,
        String nationalityCode,
        Boolean moroccanResident,
        Integer age,
        Money totalMonthlyIncome,
        Rate applicableRate,
        Rate rateMin,
        Rate rateMax,
        FinancialSnapshot applicable,
        RateBoundsSnapshot bounds,
        MourabahaFinancialSnapshot mourabahaSnapshot,
        List<AmortizationRow> amortizationSchedule,
        List<MourabahaAmortizationRow> mourabahaAmortizationSchedule,
        BigDecimal taeg,
        String demandReference
) {
    public MortgageCreditResponse {
        amortizationSchedule = amortizationSchedule != null ? List.copyOf(amortizationSchedule) : null;
        mourabahaAmortizationSchedule = mourabahaAmortizationSchedule != null
            ? List.copyOf(mourabahaAmortizationSchedule)
            : null;
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    public static class Builder {
        private Money amount;

        private Integer duration;

        private ProductType productType;

        private CspCode cspCode;

        private String nationalityCode;

        private Boolean moroccanResident;

        private Integer age;

        private Money totalMonthlyIncome;

        private Rate applicableRate;

        private Rate rateMin;

        private Rate rateMax;

        private FinancialSnapshot applicable;

        private RateBoundsSnapshot bounds;

        private MourabahaFinancialSnapshot mourabahaSnapshot;

        private List<AmortizationRow> amortizationSchedule;

        private List<MourabahaAmortizationRow> mourabahaAmortizationSchedule;

        private BigDecimal taeg;

        private String demandReference;

        public Builder amount(Money amount) {
            this.amount = amount;
            return this;
        }

        public Builder duration(Integer duration) {
            this.duration = duration;
            return this;
        }

        public Builder productType(ProductType productType) {
            this.productType = productType;
            return this;
        }

        public Builder cspCode(CspCode cspCode) {
            this.cspCode = cspCode;
            return this;
        }

        public Builder nationalityCode(String nationalityCode) {
            this.nationalityCode = nationalityCode;
            return this;
        }

        public Builder moroccanResident(Boolean moroccanResident) {
            this.moroccanResident = moroccanResident;
            return this;
        }

        public Builder age(Integer age) {
            this.age = age;
            return this;
        }

        public Builder totalMonthlyIncome(Money totalMonthlyIncome) {
            this.totalMonthlyIncome = totalMonthlyIncome;
            return this;
        }

        public Builder applicableRate(Rate applicableRate) {
            this.applicableRate = applicableRate;
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

        public Builder applicable(FinancialSnapshot applicable) {
            this.applicable = applicable;
            return this;
        }

        public Builder bounds(RateBoundsSnapshot bounds) {
            this.bounds = bounds;
            return this;
        }

        public Builder mourabahaSnapshot(MourabahaFinancialSnapshot mourabahaSnapshot) {
            this.mourabahaSnapshot = mourabahaSnapshot;
            return this;
        }

        public Builder amortizationSchedule(List<AmortizationRow> amortizationSchedule) {
            this.amortizationSchedule = amortizationSchedule;
            return this;
        }

        public Builder mourabahaAmortizationSchedule(List<MourabahaAmortizationRow> mourabahaAmortizationSchedule) {
            this.mourabahaAmortizationSchedule = mourabahaAmortizationSchedule;
            return this;
        }

        public Builder taeg(BigDecimal taeg) {
            this.taeg = taeg;
            return this;
        }

        public Builder demandReference(String demandReference) {
            this.demandReference = demandReference;
            return this;
        }

        public MortgageCreditResponse build() {
            return new MortgageCreditResponse(
                amount, duration, productType, cspCode, nationalityCode, moroccanResident, age, totalMonthlyIncome,
                applicableRate, rateMin, rateMax, applicable, bounds, mourabahaSnapshot, amortizationSchedule,
                mourabahaAmortizationSchedule, taeg, demandReference);
        }
    }
}
