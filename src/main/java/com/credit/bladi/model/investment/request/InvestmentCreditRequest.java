package com.credit.bladi.model.investment.request;

import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.ValueObjectMappers;
import java.math.BigDecimal;
import java.util.Objects;

public final class InvestmentCreditRequest {
    private final String companyName;

    private final EnterpriseType enterpriseType;

    private final String sector;

    private final String email;

    private final String phone;

    private final Money amount;

    private final Integer duration;

    private final Money annualRevenue;

    private final Integer companyAgeYears;

    private final Money existingAnnualDebtService;

    private final Money netAnnualResult;

    private final Money guaranteesValue;

    private InvestmentCreditRequest(Builder builder) {
        this.companyName = builder.companyName;
        this.enterpriseType = builder.enterpriseType;
        this.sector = builder.sector;
        this.email = builder.email;
        this.phone = builder.phone;
        this.amount = builder.amount;
        this.duration = builder.duration;
        this.annualRevenue = builder.annualRevenue;
        this.companyAgeYears = builder.companyAgeYears;
        this.existingAnnualDebtService = builder.existingAnnualDebtService != null
            ? builder.existingAnnualDebtService : Money.of(BigDecimal.ZERO);
        this.netAnnualResult = builder.netAnnualResult;
        this.guaranteesValue = builder.guaranteesValue;
    }

    public String getCompanyName() {
        return companyName;
    }

    public EnterpriseType getEnterpriseType() {
        return enterpriseType;
    }

    public String getSector() {
        return sector;
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

    public Money getAnnualRevenue() {
        return annualRevenue;
    }

    public Integer getCompanyAgeYears() {
        return companyAgeYears;
    }

    public Money getExistingAnnualDebtService() {
        return existingAnnualDebtService;
    }

    public Money getNetAnnualResult() {
        return netAnnualResult;
    }

    public Money getGuaranteesValue() {
        return guaranteesValue;
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    public static class Builder {
        private String companyName;

        private EnterpriseType enterpriseType;

        private String sector;

        private String email;

        private String phone;

        private Money amount;

        private Integer duration;

        private Money annualRevenue;

        private Integer companyAgeYears;

        private Money existingAnnualDebtService;

        private Money netAnnualResult;

        private Money guaranteesValue;

        public Builder companyName(String companyName) {
            this.companyName = companyName;
            return this;
        }

        public Builder enterpriseType(EnterpriseType enterpriseType) {
            this.enterpriseType = enterpriseType;
            return this;
        }

        public Builder sector(String sector) {
            this.sector = sector;
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

        public Builder annualRevenue(Money annualRevenue) {
            this.annualRevenue = annualRevenue;
            return this;
        }

        public Builder companyAgeYears(Integer companyAgeYears) {
            this.companyAgeYears = companyAgeYears;
            return this;
        }

        public Builder existingAnnualDebtService(Money existingAnnualDebtService) {
            this.existingAnnualDebtService = existingAnnualDebtService;
            return this;
        }

        public Builder netAnnualResult(Money netAnnualResult) {
            this.netAnnualResult = netAnnualResult;
            return this;
        }

        public Builder guaranteesValue(Money guaranteesValue) {
            this.guaranteesValue = guaranteesValue;
            return this;
        }

        public InvestmentCreditRequest build() {
            validateDuration(duration);
            validateCompanyAge(companyAgeYears);

            return new InvestmentCreditRequest(this);
        }

        private static void validateDuration(Integer duration) {
            if (duration != null && duration <= 0) {
                throw new IllegalArgumentException("duration must be strictly positive: " + duration);
            }
        }

        private static void validateCompanyAge(Integer companyAgeYears) {
            if (companyAgeYears != null && companyAgeYears < 0) {
                throw new IllegalArgumentException("companyAgeYears cannot be negative: " + companyAgeYears);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InvestmentCreditRequest other)) {
            return false;
        }
        return Objects.equals(companyName, other.companyName)
            && enterpriseType == other.enterpriseType
            && Objects.equals(sector, other.sector)
            && Objects.equals(email, other.email)
            && Objects.equals(phone, other.phone)
            && ValueObjectMappers.moneyEquals(amount, other.amount)
            && Objects.equals(duration, other.duration)
            && ValueObjectMappers.moneyEquals(annualRevenue, other.annualRevenue)
            && Objects.equals(companyAgeYears, other.companyAgeYears)
            && ValueObjectMappers.moneyEquals(existingAnnualDebtService, other.existingAnnualDebtService)
            && ValueObjectMappers.moneyEquals(netAnnualResult, other.netAnnualResult)
            && ValueObjectMappers.moneyEquals(guaranteesValue, other.guaranteesValue);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(companyName, enterpriseType, sector, email, phone, duration, companyAgeYears);
        result = 31 * result + ValueObjectMappers.moneyHashCode(amount);
        result = 31 * result + ValueObjectMappers.moneyHashCode(annualRevenue);
        result = 31 * result + ValueObjectMappers.moneyHashCode(existingAnnualDebtService);
        result = 31 * result + ValueObjectMappers.moneyHashCode(netAnnualResult);
        result = 31 * result + ValueObjectMappers.moneyHashCode(guaranteesValue);
        return result;
    }

    @Override
    public String toString() {
        return String.format(
            "InvestmentCreditRequest [companyName=%s, enterpriseType=%s, sector=%s, email=%s, phone=%s, "
                + "amount=%s, duration=%s, annualRevenue=%s, companyAgeYears=%s, existingAnnualDebtService=%s, "
                + "netAnnualResult=%s, guaranteesValue=%s]",
            companyName, enterpriseType, sector, email, phone, amount, duration, annualRevenue, companyAgeYears,
            existingAnnualDebtService, netAnnualResult, guaranteesValue);
    }
}
