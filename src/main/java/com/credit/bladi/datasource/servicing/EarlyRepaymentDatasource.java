package com.credit.bladi.datasource.servicing;

import com.credit.bladi.datasource.servicing.entity.EarlyRepaymentEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EarlyRepaymentDatasource extends JpaRepository<EarlyRepaymentEntity, Long> {
    List<EarlyRepaymentEntity> findByCreditFileIdOrderByPerformedAtAsc(Long creditFileId);
}
