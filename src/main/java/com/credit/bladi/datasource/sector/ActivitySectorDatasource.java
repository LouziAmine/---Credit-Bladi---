package com.credit.bladi.datasource.sector;

import com.credit.bladi.datasource.sector.entity.ActivitySectorEntity;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivitySectorDatasource extends JpaRepository<ActivitySectorEntity, Long> {
    List<ActivitySectorEntity> findAll(Sort sort);

    boolean existsByNameIgnoreCase(String name);
}
