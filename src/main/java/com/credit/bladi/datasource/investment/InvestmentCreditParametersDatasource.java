package com.credit.bladi.datasource.investment;

import com.credit.bladi.datasource.investment.entity.InvestmentCreditParametersEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InvestmentCreditParametersDatasource extends JpaRepository<InvestmentCreditParametersEntity, Long> {
    Optional<InvestmentCreditParametersEntity> findByPublicId(UUID publicId);

    @Query("SELECT e FROM InvestmentCreditParametersEntity e WHERE e.enterpriseType = :enterpriseType "
        + "AND e.active = true")
    Optional<InvestmentCreditParametersEntity> findActiveByEnterpriseType(
        @Param("enterpriseType") String enterpriseType);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE InvestmentCreditParametersEntity e SET e.active = false, e.version = e.version + 1 "
        + "WHERE e.enterpriseType = :enterpriseType AND e.id <> :excludedId AND e.active = true")
    void deactivateOthersForEnterpriseType(
        @Param("enterpriseType") String enterpriseType, @Param("excludedId") Long excludedId);
}
