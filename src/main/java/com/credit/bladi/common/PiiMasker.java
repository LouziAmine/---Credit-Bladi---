package com.credit.bladi.common;

public final class PiiMasker {
    private static final int VISIBLE_PHONE_DIGITS = 4;

    private PiiMasker() {
    }

    public static String maskEmail(String email) {
        if (email == null || email.isBlank()) {
            return email;
        }
        int at = email.indexOf('@');
        if (at <= 0) {
            return "***";
        }
        return email.charAt(0) + "***" + email.substring(at);
    }

    public static String maskPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return phone;
        }
        if (phone.length() <= VISIBLE_PHONE_DIGITS) {
            return "***";
        }
        return "***" + phone.substring(phone.length() - VISIBLE_PHONE_DIGITS);
    }
}
