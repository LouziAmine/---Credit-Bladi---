package com.credit.bladi.datasource.creditrate;

import com.credit.bladi.datasource.creditrate.entity.CreditRateEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CreditRateDatasource extends JpaRepository<CreditRateEntity, Long> {

    @Query("SELECT e FROM CreditRateEntity e WHERE e.cspCode = :cspCode AND e.active = true")
    Optional<CreditRateEntity> findActiveByCspCode(@Param("cspCode") String cspCode);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE CreditRateEntity e SET e.active = false, e.version = e.version + 1 "
        + "WHERE e.cspCode = :cspCode AND e.id <> :excludedId AND e.active = true")
    void deactivateOthersForCspCode(@Param("cspCode") String cspCode, @Param("excludedId") Long excludedId);
}