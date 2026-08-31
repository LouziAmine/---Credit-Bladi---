package com.credit.bladi.common;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Consumer;

public final class MergeSupport {
    private MergeSupport() {
    }

    public static <T> void updateIfDifferent(Consumer<T> setter, T current, T incoming) {
        if (Objects.nonNull(incoming) && !valuesEqual(current, incoming)) {
            setter.accept(incoming);
        }
    }

    public static boolean valuesEqual(Object current, Object incoming) {
        if (current instanceof BigDecimal currentValue && incoming instanceof BigDecimal incomingValue) {
            return currentValue.compareTo(incomingValue) == 0;
        }
        return Objects.equals(current, incoming);
    }
}
