package com.credit.bladi.repository.sector;

import com.credit.bladi.datasource.sector.ActivitySectorDatasource;
import com.credit.bladi.datasource.sector.entity.ActivitySectorEntity;
import com.credit.bladi.datasource.sector.mapper.ActivitySectorEntityMapper;
import com.credit.bladi.model.sector.ActivitySectorModel;
import java.time.Clock;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class ActivitySectorRepository implements IActivitySectorRepository {
    private final ActivitySectorDatasource datasource;

    private final Clock clock;

    public ActivitySectorRepository(ActivitySectorDatasource datasource, Clock clock) {
        this.clock = clock;
        this.datasource = datasource;
    }

    @Override
    public ActivitySectorModel create(String name) {
        ActivitySectorEntity entity = ActivitySectorEntityMapper.mapToNewEntity(name);
        entity.setCreatedAt(clock.instant());
        datasource.save(entity);
        return ActivitySectorEntityMapper.mapFromEntity(entity);
    }

    @Override
    public List<ActivitySectorModel> findAll() {
        return datasource.findAll(Sort.by("name")).stream()
            .map(ActivitySectorEntityMapper::mapFromEntity)
            .toList();
    }

    @Override
    public boolean existsByNameIgnoreCase(String name) {
        return datasource.existsByNameIgnoreCase(name);
    }
}
