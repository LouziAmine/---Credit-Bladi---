package com.credit.bladi.datasource.borrower;

import com.credit.bladi.datasource.borrower.entity.MortgageBorrowerEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MortgageBorrowerDatasource extends JpaRepository<MortgageBorrowerEntity, Long> {
    List<MortgageBorrowerEntity> findByUserIdOrderByLinkedAtDesc(Long userId);

    boolean existsByDemandIdAndUserId(Long demandId, Long userId);
}
