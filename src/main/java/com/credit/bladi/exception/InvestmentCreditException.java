package com.credit.bladi.exception;

import java.math.BigDecimal;

public class InvestmentCreditException extends ApiException {
    private final InvestmentCreditExceptionType type;

    public enum InvestmentCreditExceptionType {
        PARAMETERS_NOT_FOUND,
        COMPANY_AGE_INSUFFICIENT,
        FINANCING_RATIO_EXCEEDED,
        GUARANTEE_COVERAGE_INSUFFICIENT,
        DEBT_SERVICE_RATIO_EXCEEDED,
        DEMAND_RATE_NOT_SET
    }

    private InvestmentCreditException(InvestmentCreditExceptionType type, ApiStatus status, String messageKey,
            Object... args) {
        super(status, messageKey, args);
        this.type = type;
    }

    public static InvestmentCreditException of(InvestmentCreditExceptionType type) {
        return switch (type) {
            case PARAMETERS_NOT_FOUND -> new InvestmentCreditException(type, ApiStatus.SERVICE_UNAVAILABLE,
                "investmentCredit.parametersNotFound");
            case DEMAND_RATE_NOT_SET -> new InvestmentCreditException(type, ApiStatus.CONFLICT,
                "investmentCredit.demandRateNotSet");
            case COMPANY_AGE_INSUFFICIENT, FINANCING_RATIO_EXCEEDED, GUARANTEE_COVERAGE_INSUFFICIENT,
                 DEBT_SERVICE_RATIO_EXCEEDED -> throw new IllegalArgumentException(
                type + " requires the configured ratio/value: use of(type, detail) instead");
        };
    }

    public static InvestmentCreditException of(InvestmentCreditExceptionType type, BigDecimal ratio) {
        BigDecimal percentage = ratio.multiply(BigDecimal.valueOf(100));
        return switch (type) {
            case FINANCING_RATIO_EXCEEDED -> new InvestmentCreditException(type, ApiStatus.UNPROCESSABLE_CONTENT,
                "investmentCredit.financingRatioExceeded", plain(percentage));
            case GUARANTEE_COVERAGE_INSUFFICIENT -> new InvestmentCreditException(type,
                ApiStatus.UNPROCESSABLE_CONTENT,
                "investmentCredit.guaranteeCoverageInsufficient", plain(percentage));
            case DEBT_SERVICE_RATIO_EXCEEDED -> new InvestmentCreditException(type, ApiStatus.UNPROCESSABLE_CONTENT,
                "investmentCredit.debtServiceRatioExceeded", plain(percentage));
            case PARAMETERS_NOT_FOUND, COMPANY_AGE_INSUFFICIENT,
                 DEMAND_RATE_NOT_SET -> throw new IllegalArgumentException(
                type + " does not take a ratio parameter: use of(type) or of(type, minYears) instead");
        };
    }

    public static InvestmentCreditException of(InvestmentCreditExceptionType type, int minCompanyAgeYears) {
        if (type != InvestmentCreditExceptionType.COMPANY_AGE_INSUFFICIENT) {
            throw new IllegalArgumentException(type + " does not take a minCompanyAgeYears parameter");
        }
        return new InvestmentCreditException(type, ApiStatus.UNPROCESSABLE_CONTENT,
            "investmentCredit.companyAgeInsufficient", minCompanyAgeYears);
    }

    private static String plain(BigDecimal value) {
        return value.stripTrailingZeros().toPlainString();
    }

    public InvestmentCreditExceptionType getType() {
        return type;
    }
}
