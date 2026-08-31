package com.credit.bladi.controller.sector.mapper;

import com.credit.bladi.controller.dto.sector.response.ActivitySectorResponseDTO;
import com.credit.bladi.model.sector.ActivitySectorModel;

public final class ActivitySectorControllerMapper {
    private ActivitySectorControllerMapper() {
    }

    public static ActivitySectorResponseDTO toResponseDTO(ActivitySectorModel model) {
        return new ActivitySectorResponseDTO(model.publicId(), model.name(), model.createdAt());
    }
}
