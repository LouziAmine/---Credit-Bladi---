package com.credit.bladi.model.parameters;

import com.credit.bladi.model.vo.Rate;
import java.util.Optional;

public interface VersionedParametersModel<M extends VersionedParametersModel<M>> {
    Long getId();

    String describeChangesFrom(M before);

    default Optional<Rate> highestAnnualRate() {
        return Optional.empty();
    }
}
