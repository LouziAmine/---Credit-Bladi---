package com.credit.bladi.datasource.client;

import com.credit.bladi.datasource.client.entity.EnterpriseEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EnterpriseDatasource extends JpaRepository<EnterpriseEntity, Long> {
    Optional<EnterpriseEntity> findByPublicId(UUID publicId);

    List<EnterpriseEntity> findByNormalizedNameOrderByCreatedAtAsc(String normalizedName);

    Optional<EnterpriseEntity> findByRegistrationNumber(String registrationNumber);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE EnterpriseEntity e SET e.rib = :rib WHERE e.id = :id")
    int updateRib(@Param("id") Long id, @Param("rib") String rib);
}
