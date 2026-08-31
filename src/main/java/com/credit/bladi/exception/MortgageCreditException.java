package com.credit.bladi.exception;

public class MortgageCreditException extends ApiException {
    private final MortgageCreditExceptionType type;

    public enum MortgageCreditExceptionType {
        CSP_NOT_FOUND,
        STRATEGY_NOT_FOUND,
        BAM_PARAMETERS_NOT_FOUND,
        MOURABAHA_PARAMETERS_NOT_FOUND,
        DEMAND_RATE_NOT_SET
    }

    private MortgageCreditException(MortgageCreditExceptionType type, ApiStatus status, String messageKey) {
        super(status, messageKey);
        this.type = type;
    }

    public static MortgageCreditException of(MortgageCreditExceptionType type) {
        return switch (type) {
            case CSP_NOT_FOUND -> new MortgageCreditException(type, ApiStatus.NOT_FOUND, "mortgageCredit.cspNotFound");
            case STRATEGY_NOT_FOUND ->
                new MortgageCreditException(type, ApiStatus.INTERNAL_SERVER_ERROR, "mortgageCredit.strategyNotFound");
            case BAM_PARAMETERS_NOT_FOUND -> new MortgageCreditException(type, ApiStatus.SERVICE_UNAVAILABLE,
                "mortgageCredit.bamParametersNotFound");
            case MOURABAHA_PARAMETERS_NOT_FOUND -> new MortgageCreditException(type, ApiStatus.SERVICE_UNAVAILABLE,
                "mortgageCredit.mourabahaParametersNotFound");
            case DEMAND_RATE_NOT_SET -> new MortgageCreditException(type, ApiStatus.CONFLICT,
                "mortgageCredit.demandRateNotSet");
        };
    }

    public MortgageCreditExceptionType getType() {
        return type;
    }
}
