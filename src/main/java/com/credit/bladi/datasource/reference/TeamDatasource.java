package com.credit.bladi.datasource.reference;

import com.credit.bladi.datasource.reference.entity.TeamEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamDatasource extends JpaRepository<TeamEntity, Long> {
    Optional<TeamEntity> findByCode(String code);
}
