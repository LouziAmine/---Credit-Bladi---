package com.credit.bladi.repository.sector;

import com.credit.bladi.model.sector.ActivitySectorModel;
import java.util.List;

public interface IActivitySectorRepository {
    ActivitySectorModel create(String name);

    List<ActivitySectorModel> findAll();

    boolean existsByNameIgnoreCase(String name);
}
