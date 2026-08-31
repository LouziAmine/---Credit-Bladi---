package com.credit.bladi.controller.reference.mapper;

import com.credit.bladi.controller.dto.reference.response.GradeResponseDTO;
import com.credit.bladi.controller.dto.reference.response.TeamResponseDTO;
import com.credit.bladi.model.reference.Grade;
import com.credit.bladi.model.reference.Team;

public final class ReferenceDataMapper {
    private ReferenceDataMapper() {
    }

    public static GradeResponseDTO toResponseDTO(Grade grade) {
        return new GradeResponseDTO(grade.publicId(), grade.code(), grade.name());
    }

    public static TeamResponseDTO toResponseDTO(Team team) {
        return new TeamResponseDTO(team.publicId(), team.code(), team.name());
    }
}
