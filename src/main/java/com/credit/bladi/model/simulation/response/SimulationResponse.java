package com.credit.bladi.model.simulation.response;

import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.model.mourabaha.MourabahaAmortizationRow;
import com.credit.bladi.model.mourabaha.MourabahaFinancialSnapshot;
import com.credit.bladi.model.simulation.AmortizationRow;
import com.credit.bladi.model.simulation.FinancialSnapshot;
import com.credit.bladi.model.simulation.RateBoundsSnapshot;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class SimulationResponse {

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

    private SimulationResponse() {
    }

    public Money getAmount() {
        return amount;
    }

    public Integer getDuration() {
        return duration;
    }

    public ProductType getProductType() {
        return productType;
    }

    public CspCode getCspCode() {
        return cspCode;
    }

    public String getNationalityCode() {
        return nationalityCode;
    }

    public Boolean getMoroccanResident() {
        return moroccanResident;
    }

    public Integer getAge() {
        return age;
    }

    public Money getTotalMonthlyIncome() {
        return totalMonthlyIncome;
    }

    public Rate getApplicableRate() {
        return applicableRate;
    }

    public Rate getRateMin() {
        return rateMin;
    }

    public Rate getRateMax() {
        return rateMax;
    }

    public FinancialSnapshot getApplicable() {
        return applicable;
    }

    public RateBoundsSnapshot getBounds() {
        return bounds;
    }

    public MourabahaFinancialSnapshot getMourabahaSnapshot() {
        return mourabahaSnapshot;
    }

    public List<AmortizationRow> getAmortizationSchedule() {
        return amortizationSchedule;
    }

    public List<MourabahaAmortizationRow> getMourabahaAmortizationSchedule() {
        return mourabahaAmortizationSchedule;
    }

    public BigDecimal getTaeg() {
        return taeg;
    }

    private void setAmount(Money amount) {
        this.amount = amount;
    }

    private void setDuration(Integer duration) {
        this.duration = duration;
    }

    private void setProductType(ProductType productType) {
        this.productType = productType;
    }

    private void setCspCode(CspCode cspCode) {
        this.cspCode = cspCode;
    }

    private void setNationalityCode(String nationalityCode) {
        this.nationalityCode = nationalityCode;
    }

    private void setMoroccanResident(Boolean moroccanResident) {
        this.moroccanResident = moroccanResident;
    }

    private void setAge(Integer age) {
        this.age = age;
    }

    private void setTotalMonthlyIncome(Money totalMonthlyIncome) {
        this.totalMonthlyIncome = totalMonthlyIncome;
    }

    private void setApplicableRate(Rate applicableRate) {
        this.applicableRate = applicableRate;
    }

    private void setRateMin(Rate rateMin) {
        this.rateMin = rateMin;
    }

    private void setRateMax(Rate rateMax) {
        this.rateMax = rateMax;
    }

    private void setApplicable(FinancialSnapshot applicable) {
        this.applicable = applicable;
    }

    private void setBounds(RateBoundsSnapshot bounds) {
        this.bounds = bounds;
    }

    private void setMourabahaSnapshot(MourabahaFinancialSnapshot mourabahaSnapshot) {
        this.mourabahaSnapshot = mourabahaSnapshot;
    }

    private void setAmortizationSchedule(List<AmortizationRow> amortizationSchedule) {
        this.amortizationSchedule = amortizationSchedule;
    }

    private void setMourabahaAmortizationSchedule(List<MourabahaAmortizationRow> mourabahaAmortizationSchedule) {
        this.mourabahaAmortizationSchedule = mourabahaAmortizationSchedule;
    }

    private void setTaeg(BigDecimal taeg) {
        this.taeg = taeg;
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

        public SimulationResponse build() {
            SimulationResponse simulationResponse = new SimulationResponse();
            simulationResponse.setAmount(amount);
            simulationResponse.setDuration(duration);
            simulationResponse.setProductType(productType);
            simulationResponse.setCspCode(cspCode);
            simulationResponse.setNationalityCode(nationalityCode);
            simulationResponse.setMoroccanResident(moroccanResident);
            simulationResponse.setAge(age);
            simulationResponse.setTotalMonthlyIncome(totalMonthlyIncome);
            simulationResponse.setApplicableRate(applicableRate);
            simulationResponse.setRateMin(rateMin);
            simulationResponse.setRateMax(rateMax);
            simulationResponse.setApplicable(applicable);
            simulationResponse.setBounds(bounds);
            simulationResponse.setMourabahaSnapshot(mourabahaSnapshot);
            simulationResponse.setAmortizationSchedule(amortizationSchedule);
            simulationResponse.setMourabahaAmortizationSchedule(mourabahaAmortizationSchedule);
            simulationResponse.setTaeg(taeg);
            return simulationResponse;
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SimulationResponse other)) {
            return false;
        }
        return moneyEquals(amount, other.amount)
            && Objects.equals(duration, other.duration)
            && productType == other.productType
            && cspCode == other.cspCode
            && Objects.equals(nationalityCode, other.nationalityCode)
            && Objects.equals(moroccanResident, other.moroccanResident)
            && Objects.equals(age, other.age)
            && moneyEquals(totalMonthlyIncome, other.totalMonthlyIncome)
            && rateEquals(applicableRate, other.applicableRate)
            && rateEquals(rateMin, other.rateMin)
            && rateEquals(rateMax, other.rateMax)
            && snapshotEquals(applicable, other.applicable)
            && boundsEquals(bounds, other.bounds)
            && mourabahaSnapshotEquals(mourabahaSnapshot, other.mourabahaSnapshot)
            && scheduleEquals(amortizationSchedule, other.amortizationSchedule)
            && mourabahaScheduleEquals(mourabahaAmortizationSchedule, other.mourabahaAmortizationSchedule)
            && bigDecimalEquals(taeg, other.taeg);
    }

    private static boolean moneyEquals(Money a, Money b) {
        return a == null ? b == null : a.equalsValue(b);
    }

    private static boolean rateEquals(Rate a, Rate b) {
        return a == null ? b == null : a.equalsValue(b);
    }

    private static boolean snapshotEquals(FinancialSnapshot a, FinancialSnapshot b) {
        return a == null ? b == null : a.equalsValue(b);
    }

    private static boolean boundsEquals(RateBoundsSnapshot a, RateBoundsSnapshot b) {
        return a == null ? b == null : a.equalsValue(b);
    }

    private static boolean mourabahaSnapshotEquals(MourabahaFinancialSnapshot a, MourabahaFinancialSnapshot b) {
        return a == null ? b == null : a.equalsValue(b);
    }

    private static boolean bigDecimalEquals(BigDecimal a, BigDecimal b) {
        return a == null ? b == null : b != null && a.compareTo(b) == 0;
    }

    private static boolean scheduleEquals(List<AmortizationRow> a, List<AmortizationRow> b) {
        if (a == null || b == null) {
            return a == b;
        }
        if (a.size() != b.size()) {
            return false;
        }
        for (int i = 0; i < a.size(); i++) {
            if (!a.get(i).equalsValue(b.get(i))) {
                return false;
            }
        }
        return true;
    }

    private static boolean mourabahaScheduleEquals(List<MourabahaAmortizationRow> a, List<MourabahaAmortizationRow> b) {
        if (a == null || b == null) {
            return a == b;
        }
        if (a.size() != b.size()) {
            return false;
        }
        for (int i = 0; i < a.size(); i++) {
            if (!a.get(i).equalsValue(b.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(duration, productType, cspCode, nationalityCode, moroccanResident, age);
        result = 31 * result + moneyHash(amount);
        result = 31 * result + moneyHash(totalMonthlyIncome);
        result = 31 * result + rateHash(applicableRate);
        result = 31 * result + rateHash(rateMin);
        result = 31 * result + rateHash(rateMax);
        result = 31 * result + snapshotHash(applicable);
        result = 31 * result + boundsHash(bounds);
        result = 31 * result + mourabahaSnapshotHash(mourabahaSnapshot);
        result = 31 * result + scheduleHash(amortizationSchedule);
        result = 31 * result + mourabahaScheduleHash(mourabahaAmortizationSchedule);
        result = 31 * result + bigDecimalHash(taeg);
        return result;
    }

    private static int moneyHash(Money m) {
        return m == null ? 0 : m.valueHashCode();
    }

    private static int rateHash(Rate r) {
        return r == null ? 0 : r.valueHashCode();
    }

    private static int snapshotHash(FinancialSnapshot s) {
        return s == null ? 0 : s.valueHashCode();
    }

    private static int boundsHash(RateBoundsSnapshot b) {
        return b == null ? 0 : b.valueHashCode();
    }

    private static int mourabahaSnapshotHash(MourabahaFinancialSnapshot s) {
        return s == null ? 0 : s.valueHashCode();
    }

    private static int bigDecimalHash(BigDecimal b) {
        return b == null ? 0 : b.stripTrailingZeros().hashCode();
    }

    private static int scheduleHash(List<AmortizationRow> list) {
        if (list == null) {
            return 0;
        }
        int result = 1;
        for (AmortizationRow row : list) {
            result = 31 * result + row.valueHashCode();
        }
        return result;
    }

    private static int mourabahaScheduleHash(List<MourabahaAmortizationRow> list) {
        if (list == null) {
            return 0;
        }
        int result = 1;
        for (MourabahaAmortizationRow row : list) {
            result = 31 * result + row.valueHashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format(
            "SimulationResponse [amount=%s, duration=%s, productType=%s, cspCode=%s, nationalityCode=%s, "
                + "moroccanResident=%s, age=%s, totalMonthlyIncome=%s, "
                + "applicableRate=%s, rateMin=%s, rateMax=%s, applicable=%s, bounds=%s, mourabahaSnapshot=%s, "
                + "amortizationScheduleSize=%s, mourabahaAmortizationScheduleSize=%s, taeg=%s]",
            amount, duration, productType, cspCode, nationalityCode, moroccanResident, age, totalMonthlyIncome,
            applicableRate, rateMin, rateMax, applicable, bounds, mourabahaSnapshot,
            amortizationSchedule == null ? null : amortizationSchedule.size(),
            mourabahaAmortizationSchedule == null ? null : mourabahaAmortizationSchedule.size(),
            taeg);
    }
}
