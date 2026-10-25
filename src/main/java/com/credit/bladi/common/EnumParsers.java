package com.credit.bladi.common;

import com.credit.bladi.exception.ValidationException;
import com.credit.bladi.exception.ValidationException.ValidationExceptionType;

public final class EnumParsers {
    private EnumParsers() {
    }

    public static <E extends Enum<E>> E parse(Class<E> enumType, String value, String fieldName) {
        try {
            return Enum.valueOf(enumType, value);
        } catch (IllegalArgumentException e) {
            throw ValidationException.of(ValidationExceptionType.INVALID_VALUE, fieldName + ": " + value);
        }
    }

    public static <E extends Enum<E>> E parseNullable(Class<E> enumType, String value, String fieldName) {
        return (value == null || value.isBlank()) ? null : parse(enumType, value, fieldName);
    }
}
