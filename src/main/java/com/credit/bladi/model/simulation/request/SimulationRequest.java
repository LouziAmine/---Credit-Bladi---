package com.credit.bladi.model.simulation.request;

import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.model.vo.Money;
import java.math.BigDecimal;
import java.util.Objects;

public class SimulationRequest {

    private Money amount;

    private Integer duration;

    private ProductType productType;

    private CspCode cspCode;

    private String nationalityCode;

    private Boolean moroccanResident;

    private Money propertyValue;

    private Money monthlyIncome;

    private Integer age;

    private Boolean coBorrower;

    private Money coBorrowerMonthlyIncome;

    private SimulationRequest() {
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

    public Money getPropertyValue() {
        return propertyValue;
    }

    public Money getMonthlyIncome() {
        return monthlyIncome;
    }

    public Integer getAge() {
        return age;
    }

    public Boolean getCoBorrower() {
        return coBorrower;
    }

    public Money getCoBorrowerMonthlyIncome() {
        return coBorrowerMonthlyIncome;
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

    private void setPropertyValue(Money propertyValue) {
        this.propertyValue = propertyValue;
    }

    private void setMonthlyIncome(Money monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    private void setAge(Integer age) {
        this.age = age;
    }

    private void setCoBorrower(Boolean coBorrower) {
        this.coBorrower = coBorrower;
    }

    private void setCoBorrowerMonthlyIncome(Money coBorrowerMonthlyIncome) {
        this.coBorrowerMonthlyIncome = coBorrowerMonthlyIncome;
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

        private Money propertyValue;

        private Money monthlyIncome;

        private Integer age;

        private Boolean coBorrower;

        private Money coBorrowerMonthlyIncome;

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

        public Builder propertyValue(Money propertyValue) {
            this.propertyValue = propertyValue;
            return this;
        }

        public Builder monthlyIncome(Money monthlyIncome) {
            this.monthlyIncome = monthlyIncome;
            return this;
        }

        public Builder age(Integer age) {
            this.age = age;
            return this;
        }

        public Builder coBorrower(Boolean coBorrower) {
            this.coBorrower = coBorrower;
            return this;
        }

        public Builder coBorrowerMonthlyIncome(Money coBorrowerMonthlyIncome) {
            this.coBorrowerMonthlyIncome = coBorrowerMonthlyIncome;
            return this;
        }

        public SimulationRequest build() {
            validateDuration(duration);
            validateAge(age);

            SimulationRequest req = new SimulationRequest();
            req.setAmount(amount);
            req.setDuration(duration);
            req.setProductType(productType != null ? productType : ProductType.CONVENTIONAL);
            req.setCspCode(cspCode);
            req.setNationalityCode(nationalityCode);
            req.setMoroccanResident(moroccanResident);
            req.setPropertyValue(propertyValue);
            req.setMonthlyIncome(monthlyIncome);
            req.setAge(age);
            req.setCoBorrower(coBorrower != null && coBorrower);
            req.setCoBorrowerMonthlyIncome(coBorrowerMonthlyIncome != null ? coBorrowerMonthlyIncome
                : Money.of(BigDecimal.ZERO));
            return req;
        }

        private static void validateDuration(Integer duration) {
            if (duration != null && duration <= 0) {
                throw new IllegalArgumentException("duration must be strictly positive: " + duration);
            }
        }

        private static void validateAge(Integer age) {
            if (age != null && age <= 0) {
                throw new IllegalArgumentException("age must be strictly positive: " + age);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SimulationRequest other)) {
            return false;
        }
        return moneyEquals(amount, other.amount)
            && Objects.equals(duration, other.duration)
            && productType == other.productType
            && cspCode == other.cspCode
            && Objects.equals(nationalityCode, other.nationalityCode)
            && Objects.equals(moroccanResident, other.moroccanResident)
            && moneyEquals(propertyValue, other.propertyValue)
            && moneyEquals(monthlyIncome, other.monthlyIncome)
            && Objects.equals(age, other.age)
            && Objects.equals(coBorrower, other.coBorrower)
            && moneyEquals(coBorrowerMonthlyIncome, other.coBorrowerMonthlyIncome);
    }

    private static boolean moneyEquals(Money a, Money b) {
        return a == null ? b == null : a.equalsValue(b);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(duration, productType, cspCode, nationalityCode, moroccanResident, age, coBorrower);
        result = 31 * result + moneyHash(amount);
        result = 31 * result + moneyHash(propertyValue);
        result = 31 * result + moneyHash(monthlyIncome);
        result = 31 * result + moneyHash(coBorrowerMonthlyIncome);
        return result;
    }

    private static int moneyHash(Money m) {
        return m == null ? 0 : m.valueHashCode();
    }

    @Override
    public String toString() {
        return String.format(
            "SimulationRequest [amount=%s, duration=%s, productType=%s, cspCode=%s, nationalityCode=%s, "
                + "moroccanResident=%s, propertyValue=%s, monthlyIncome=%s, age=%s, coBorrower=%s, "
                + "coBorrowerMonthlyIncome=%s]",
            amount, duration, productType, cspCode, nationalityCode, moroccanResident, propertyValue, monthlyIncome,
            age, coBorrower, coBorrowerMonthlyIncome);
    }
}