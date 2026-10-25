package com.credit.bladi.model.notification;

import java.util.Arrays;
import java.util.Objects;

public record LocalizedText(String key, Object... args) {
    public LocalizedText {
        args = args != null ? args.clone() : new Object[0];
    }

    public static LocalizedText of(String key, Object... args) {
        return new LocalizedText(key, args);
    }

    @Override
    public Object[] args() {
        return args.clone();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof LocalizedText other && Objects.equals(key, other.key) && Arrays.equals(args, other.args);
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hashCode(key) + Arrays.hashCode(args);
    }

    @Override
    public String toString() {
        return "LocalizedText[key=" + key + ", args=" + Arrays.toString(args) + "]";
    }
}
