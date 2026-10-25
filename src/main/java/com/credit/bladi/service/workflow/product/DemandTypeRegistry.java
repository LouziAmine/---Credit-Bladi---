package com.credit.bladi.service.workflow.product;

import com.credit.bladi.model.enums.DemandType;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class DemandTypeRegistry<T extends DemandTypeStrategy> {
    private final Map<DemandType, T> strategiesByType = new EnumMap<>(DemandType.class);

    protected DemandTypeRegistry(List<T> strategies, String strategyKind) {
        for (T strategy : strategies) {
            T previous = strategiesByType.put(strategy.supportedType(), strategy);
            if (previous != null) {
                throw new IllegalStateException("Two " + strategyKind + " registered for "
                    + strategy.supportedType() + ": " + previous.getClass().getSimpleName() + " and "
                    + strategy.getClass().getSimpleName());
            }
        }
        Set<DemandType> missing = EnumSet.allOf(DemandType.class);
        missing.removeAll(strategiesByType.keySet());
        if (!missing.isEmpty()) {
            throw new IllegalStateException("No " + strategyKind + " registered for demand type(s) " + missing);
        }
    }

    public T get(DemandType demandType) {
        return strategiesByType.get(demandType);
    }
}
