package com.credit.bladi.validator;

public final class ValidationConstraints {

    private ValidationConstraints() {
    }

    public static final String AMOUNT_MIN = "70000";
    public static final String AMOUNT_MAX = "30000000";

    public static final int DURATION_MIN = 12;
    public static final int DURATION_MAX = 300;

    public static final String CSP_CODE_REGEX = "^(RETR|SALA|FONC|PROF|INDE)$";

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
}