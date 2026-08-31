package com.credit.bladi.service.sector;

import com.credit.bladi.model.sector.ActivitySectorModel;
import java.util.List;

public interface IActivitySectorService {
    ActivitySectorModel createActivitySector(String name);

    List<ActivitySectorModel> getAllActivitySectors();
}
