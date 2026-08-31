package com.credit.bladi.datasource.mourabaha;

import com.credit.bladi.datasource.mourabaha.entity.MourabahaParametersEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MourabahaParametersDatasource extends JpaRepository<MourabahaParametersEntity, Long> {
    Optional<MourabahaParametersEntity> findByPublicId(UUID publicId);

    @Query("SELECT e FROM MourabahaParametersEntity e WHERE e.active = true ORDER BY e.id DESC LIMIT 1")
    Optional<MourabahaParametersEntity> findActiveParameters();

    long countByActiveTrue();

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE MourabahaParametersEntity e SET e.active = false, e.version = e.version + 1 "
        + "WHERE e.id <> :excludedId AND e.active = true")
    void deactivateAllExcept(@Param("excludedId") Long excludedId);
}
