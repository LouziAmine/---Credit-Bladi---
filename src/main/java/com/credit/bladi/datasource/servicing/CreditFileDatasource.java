package com.credit.bladi.datasource.servicing;

import com.credit.bladi.datasource.servicing.entity.CreditFileEntity;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditFileDatasource extends JpaRepository<CreditFileEntity, Long> {
    Optional<CreditFileEntity> findByDemandId(Long demandId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM CreditFileEntity e WHERE e.demandId = :demandId")
    Optional<CreditFileEntity> findByDemandIdForUpdate(@Param("demandId") Long demandId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE CreditFileEntity e SET e.status = com.credit.bladi.model.enums.CreditFileStatus.CLOSED "
        + "WHERE e.id = :id AND e.status = com.credit.bladi.model.enums.CreditFileStatus.ACTIVE")
    int closeIfActive(@Param("id") Long id);
}
