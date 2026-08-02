package com.credit.bladi.exception;

public class SimulationException extends ApiException {

    private final SimulationExceptionType type;

    public enum SimulationExceptionType {
        CSP_NOT_FOUND,
        STRATEGY_NOT_FOUND,
        BAM_PARAMETERS_NOT_FOUND,
        MOURABAHA_PARAMETERS_NOT_FOUND
    }

    private SimulationException(SimulationExceptionType type, ApiStatus status) {
        super(status);
        this.type = type;
    }

    public static SimulationException of(SimulationExceptionType type) {
        return switch (type) {
            case CSP_NOT_FOUND -> new SimulationException(type, ApiStatus.NOT_FOUND);
            case STRATEGY_NOT_FOUND -> new SimulationException(type, ApiStatus.INTERNAL_SERVER_ERROR);
            case BAM_PARAMETERS_NOT_FOUND -> new SimulationException(type, ApiStatus.SERVICE_UNAVAILABLE);
            case MOURABAHA_PARAMETERS_NOT_FOUND -> new SimulationException(type, ApiStatus.SERVICE_UNAVAILABLE);
        };
    }

    public SimulationExceptionType getType() {
        return type;
    }

    @Override
    public String getMessage() {
        return switch (type) {
            case CSP_NOT_FOUND -> "CSP not supported or inactive.";
            case STRATEGY_NOT_FOUND -> "No calculation strategy available for this profile.";
            case BAM_PARAMETERS_NOT_FOUND -> "BAM fee parameters are not configured.";
            case MOURABAHA_PARAMETERS_NOT_FOUND -> "Mourabaha parameters are not configured.";
        };
    }
}