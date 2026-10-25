package com.credit.bladi.repository.workflow.detail;

import com.credit.bladi.model.enums.DemandType;
import com.credit.bladi.model.workflow.DemandDetail;
import com.credit.bladi.model.workflow.DemandSnapshotDetail;
import java.util.List;
import java.util.Map;

public interface DemandDetailStore {
    DemandType supportedType();

    void save(Long demandId, DemandSnapshotDetail snapshotDetail);

    DemandDetail load(Long demandId);

    Map<Long, DemandDetail> loadAll(List<Long> demandIds);

    default void attachEnterprise(Long demandId, Long enterpriseId) {
        throw new UnsupportedOperationException(
            supportedType() + " demands do not support attaching an Enterprise.");
    }
}
