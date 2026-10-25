package com.credit.bladi.datasource.workflow;

import com.credit.bladi.datasource.workflow.entity.MortgageDemandDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MortgageDemandDetailDatasource extends JpaRepository<MortgageDemandDetailEntity, Long> {
}
