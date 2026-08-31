package com.credit.bladi.controller.reference;

import static com.credit.bladi.controller.reference.mapper.ReferenceDataMapper.toResponseDTO;

import com.credit.bladi.controller.dto.reference.response.GradeResponseDTO;
import com.credit.bladi.controller.dto.reference.response.TeamResponseDTO;
import com.credit.bladi.service.reference.IReferenceDataService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@PreAuthorize("hasRole('MANAGER')")
public class ReferenceDataController implements IReferenceDataController {
    private final IReferenceDataService referenceDataService;

    public ReferenceDataController(IReferenceDataService referenceDataService) {
        this.referenceDataService = referenceDataService;
    }

    @Override
    @GetMapping("/grades")
    public ResponseEntity<List<GradeResponseDTO>> getGrades() {
        List<GradeResponseDTO> grades = referenceDataService.getGrades().stream()
            .map(grade -> toResponseDTO(grade))
            .toList();
        return ResponseEntity.ok(grades);
    }

    @Override
    @GetMapping("/teams")
    public ResponseEntity<List<TeamResponseDTO>> getTeams() {
        List<TeamResponseDTO> teams = referenceDataService.getTeams().stream()
            .map(team -> toResponseDTO(team))
            .toList();
        return ResponseEntity.ok(teams);
    }
}
