package com.credit.bladi.datasource.workflow;

import com.credit.bladi.datasource.workflow.entity.DemandCommentEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DemandCommentDatasource extends JpaRepository<DemandCommentEntity, Long> {
    @Query("SELECT e FROM DemandCommentEntity e WHERE e.demandId = :demandId ORDER BY e.createdAt ASC")
    List<DemandCommentEntity> findByDemandIdOrderByCreatedAtAsc(@Param("demandId") Long demandId);
}
