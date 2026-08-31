package com.credit.bladi.datasource.importdemand;

import com.credit.bladi.datasource.importdemand.entity.DemandImportRowErrorEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DemandImportRowErrorDatasource extends JpaRepository<DemandImportRowErrorEntity, Long> {
    List<DemandImportRowErrorEntity> findByJobExecutionIdOrderByRowNumberAsc(Long jobExecutionId);
}
