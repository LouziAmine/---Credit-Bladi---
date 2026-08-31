package com.credit.bladi.datasource.workflow;

import com.credit.bladi.datasource.workflow.entity.WorkflowEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowDatasource extends JpaRepository<WorkflowEntity, Long> {
    @Query("SELECT w FROM WorkflowEntity w WHERE w.code = :code AND w.active = true")
    Optional<WorkflowEntity> findActiveByCode(@Param("code") String code);
}
