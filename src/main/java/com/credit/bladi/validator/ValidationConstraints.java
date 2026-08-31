package com.credit.bladi.validator;

public final class ValidationConstraints {
    private ValidationConstraints() {
    }

    public static final String AMOUNT_MIN = "70000";
    public static final String AMOUNT_MAX = "30000000";

    public static final int DURATION_MIN = 12;
    public static final int DURATION_MAX = 300;

    public static final String CSP_CODE_REGEX = "^(RETR|SALA|FONC|PROF|INDE)$";

    public static final String RATE_FORMULA_REGEX = "^(MIN|MAX|AVERAGE)$";

    public static final String PRODUCT_TYPE_REGEX = "^(CONVENTIONAL|MOURABAHA)$";

    public static final String ROLE_REGEX = "^(CLIENT|MANAGER)$";

    public static final String USERNAME_REGEX = "^[A-Za-z0-9._-]+$";

    public static final String PASSWORD_COMPLEXITY_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$";

    public static final String NATIONALITY_CODE_REGEX = "^[A-Z]{2}$";

    public static final String PROPERTY_VALUE_MIN = "80000";
    public static final String PROPERTY_VALUE_MAX = "50000000";

    public static final String MONTHLY_INCOME_MIN = "1000";
    public static final String MONTHLY_INCOME_MAX = "5000000";

    public static final int AGE_MIN = 18;

    public static final int AGE_MAX = 74;

    public static final String ENTERPRISE_TYPE_REGEX = "^(TPE|AUTO_ENTREPRENEUR|PME|ETI|GE)$";

    public static final String ENTERPRISE_AMOUNT_MIN = "50000";
    public static final String ENTERPRISE_AMOUNT_MAX = "100000000";

    public static final String ENTERPRISE_ANNUAL_AMOUNT_MIN = "0";
    public static final String ENTERPRISE_ANNUAL_AMOUNT_MAX = "1000000000";

    public static final int ENTERPRISE_COMPANY_AGE_MIN = 0;
    public static final int ENTERPRISE_COMPANY_AGE_MAX = 150;

    public static final String CLASSIFICATION_OUTCOME_REGEX = "^(REAL|FAKE|SIMULATION_ONLY)$";

    public static final String ENTERPRISE_LINK_ROLE_REGEX = "^(RESPONSABLE|DELEGUE)$";
}
