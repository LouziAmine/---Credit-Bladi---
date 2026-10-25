package com.credit.bladi.model.mortgage.request;

import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.ValueObjectMappers;
import java.math.BigDecimal;
import java.util.Objects;

public final class MortgageCreditRequest {
    private final String applicantName;

    private final String email;

    private final String phone;

    private final Money amount;

    private final Integer duration;

    private final ProductType productType;

    private final CspCode cspCode;

    private final String nationalityCode;

    private final Boolean moroccanResident;

    private final Money propertyValue;

    private final Money monthlyIncome;

    private final Integer age;

    private final Boolean coBorrower;

    private final Money coBorrowerMonthlyIncome;

    private MortgageCreditRequest(Builder builder) {
        this.applicantName = builder.applicantName;
        this.email = builder.email;
        this.phone = builder.phone;
        this.amount = builder.amount;
        this.duration = builder.duration;
        this.productType = builder.productType != null ? builder.productType : ProductType.CONVENTIONAL;
        this.cspCode = builder.cspCode;
        this.nationalityCode = builder.nationalityCode;
        this.moroccanResident = builder.moroccanResident;
        this.propertyValue = builder.propertyValue;
        this.monthlyIncome = builder.monthlyIncome;
        this.age = builder.age;
        this.coBorrower = builder.coBorrower != null && builder.coBorrower;
        this.coBorrowerMonthlyIncome = builder.coBorrowerMonthlyIncome != null ? builder.coBorrowerMonthlyIncome
            : Money.of(BigDecimal.ZERO);
    }

    public String getApplicantName() {
        return applicantName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
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

    public static Builder getBuilder() {
        return new Builder();
    }

    public static class Builder {
        private String applicantName;

        private String email;

        private String phone;

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

        public Builder applicantName(String applicantName) {
            this.applicantName = applicantName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

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

        public MortgageCreditRequest build() {
            validateDuration(duration);
            validateAge(age);

            return new MortgageCreditRequest(this);
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
        if (!(o instanceof MortgageCreditRequest other)) {
            return false;
        }
        return Objects.equals(applicantName, other.applicantName)
            && Objects.equals(email, other.email)
            && Objects.equals(phone, other.phone)
            && ValueObjectMappers.moneyEquals(amount, other.amount)
            && Objects.equals(duration, other.duration)
            && productType == other.productType
            && cspCode == other.cspCode
            && Objects.equals(nationalityCode, other.nationalityCode)
            && Objects.equals(moroccanResident, other.moroccanResident)
            && ValueObjectMappers.moneyEquals(propertyValue, other.propertyValue)
            && ValueObjectMappers.moneyEquals(monthlyIncome, other.monthlyIncome)
            && Objects.equals(age, other.age)
            && Objects.equals(coBorrower, other.coBorrower)
            && ValueObjectMappers.moneyEquals(coBorrowerMonthlyIncome, other.coBorrowerMonthlyIncome);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(applicantName, email, phone, duration, productType, cspCode, nationalityCode,
            moroccanResident, age, coBorrower);
        result = 31 * result + ValueObjectMappers.moneyHashCode(amount);
        result = 31 * result + ValueObjectMappers.moneyHashCode(propertyValue);
        result = 31 * result + ValueObjectMappers.moneyHashCode(monthlyIncome);
        result = 31 * result + ValueObjectMappers.moneyHashCode(coBorrowerMonthlyIncome);
        return result;
    }

    @Override
    public String toString() {
        return String.format(
            "MortgageCreditRequest [applicantName=%s, email=%s, phone=%s, amount=%s, duration=%s, productType=%s, "
                + "cspCode=%s, nationalityCode=%s, moroccanResident=%s, propertyValue=%s, monthlyIncome=%s, age=%s, "
                + "coBorrower=%s, coBorrowerMonthlyIncome=%s]",
            applicantName, email, phone, amount, duration, productType, cspCode, nationalityCode, moroccanResident,
            propertyValue, monthlyIncome, age, coBorrower, coBorrowerMonthlyIncome);
    }
}
