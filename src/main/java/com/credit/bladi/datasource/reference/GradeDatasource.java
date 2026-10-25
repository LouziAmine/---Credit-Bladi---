package com.credit.bladi.datasource.reference;

import com.credit.bladi.datasource.reference.entity.GradeEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeDatasource extends JpaRepository<GradeEntity, Long> {
    Optional<GradeEntity> findByCode(String code);
}
