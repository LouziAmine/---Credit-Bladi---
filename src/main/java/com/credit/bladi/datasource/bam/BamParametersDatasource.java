package com.credit.bladi.datasource.bam;

import com.credit.bladi.datasource.bam.entity.BamParametersEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BamParametersDatasource extends JpaRepository<BamParametersEntity, Long> {
    Optional<BamParametersEntity> findByPublicId(UUID publicId);

    @Query("SELECT e FROM BamParametersEntity e WHERE e.active = true ORDER BY e.id DESC LIMIT 1")
    Optional<BamParametersEntity> findActiveParameters();

    long countByActiveTrue();

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE BamParametersEntity e SET e.active = false, e.version = e.version + 1 "
        + "WHERE e.id <> :excludedId AND e.active = true")
    void deactivateAllExcept(@Param("excludedId") Long excludedId);
}
