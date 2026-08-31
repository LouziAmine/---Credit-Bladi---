package com.credit.bladi.model.audit;

import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class AuditChanges {
    private final List<String> changes = new ArrayList<>();

    private AuditChanges() {
    }

    public static AuditChanges of() {
        return new AuditChanges();
    }

    public AuditChanges field(String name, Object before, Object after) {
        if (!sameValue(before, after)) {
            changes.add(name + ": " + render(before) + " -> " + render(after));
        }
        return this;
    }

    public boolean isEmpty() {
        return changes.isEmpty();
    }

    @Override
    public String toString() {
        return "changes={" + String.join("; ", changes) + "}";
    }

    private static boolean sameValue(Object before, Object after) {
        if (before instanceof BigDecimal b && after instanceof BigDecimal a) {
            return b.compareTo(a) == 0;
        }
        return Objects.equals(before, after);
    }

    private static String render(Object value) {
        return switch (value) {
            case null -> "none";
            case Money money -> money.amount().setScale(2, RoundingMode.HALF_UP).toPlainString();
            case Rate rate -> rate.value().toPlainString();
            case BigDecimal decimal -> decimal.toPlainString();
            default -> value.toString();
        };
    }
}
