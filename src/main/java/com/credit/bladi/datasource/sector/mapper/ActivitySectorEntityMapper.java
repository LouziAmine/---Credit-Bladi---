package com.credit.bladi.datasource.sector.mapper;

import com.credit.bladi.datasource.sector.entity.ActivitySectorEntity;
import com.credit.bladi.model.sector.ActivitySectorModel;

public final class ActivitySectorEntityMapper {
    private ActivitySectorEntityMapper() {
    }

    public static ActivitySectorModel mapFromEntity(ActivitySectorEntity entity) {
        return new ActivitySectorModel(entity.getId(), entity.getPublicId(), entity.getName(), entity.getCreatedAt());
    }

    public static ActivitySectorEntity mapToNewEntity(String name) {
        ActivitySectorEntity entity = new ActivitySectorEntity();
        entity.setName(name);
        return entity;
    }
}
