package com.credit.bladi.datasource.workflow;

import com.credit.bladi.datasource.workflow.entity.EnterpriseDemandDetailEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EnterpriseDemandDetailDatasource extends JpaRepository<EnterpriseDemandDetailEntity, Long> {
    @Query("SELECT e.publicId FROM EnterpriseEntity e WHERE e.id = :id")
    Optional<UUID> findEnterprisePublicId(@Param("id") Long id);
}
