package com.credit.bladi.datasource.workflow;

import com.credit.bladi.datasource.workflow.entity.DemandAssignmentHistoryEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DemandAssignmentHistoryDatasource extends JpaRepository<DemandAssignmentHistoryEntity, Long> {
    @Query("SELECT e FROM DemandAssignmentHistoryEntity e WHERE e.demandId = :demandId ORDER BY e.assignedAt ASC")
    List<DemandAssignmentHistoryEntity> findByDemandIdOrderByAssignedAtAsc(@Param("demandId") Long demandId);
}
