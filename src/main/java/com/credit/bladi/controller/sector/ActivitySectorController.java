package com.credit.bladi.controller.sector;

import static com.credit.bladi.controller.sector.mapper.ActivitySectorControllerMapper.toResponseDTO;

import com.credit.bladi.controller.dto.sector.request.CreateActivitySectorRequestDTO;
import com.credit.bladi.controller.dto.sector.response.ActivitySectorResponseDTO;
import com.credit.bladi.model.sector.ActivitySectorModel;
import com.credit.bladi.service.sector.IActivitySectorService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(ActivitySectorController.BASE_PATH)
public class ActivitySectorController implements IActivitySectorController {
    static final String BASE_PATH = "/api/v1/activity-sectors";

    private final IActivitySectorService activitySectorService;

    public ActivitySectorController(IActivitySectorService activitySectorService) {
        this.activitySectorService = activitySectorService;
    }

    @Override
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ActivitySectorResponseDTO> createActivitySector(
            @Valid @RequestBody CreateActivitySectorRequestDTO requestDTO) {
        ActivitySectorModel created = activitySectorService.createActivitySector(requestDTO.name());
        return ResponseEntity.created(URI.create(BASE_PATH + "/" + created.publicId()))
            .body(toResponseDTO(created));
    }

    @Override
    @GetMapping
    public ResponseEntity<List<ActivitySectorResponseDTO>> getAllActivitySectors() {
        List<ActivitySectorResponseDTO> sectors = activitySectorService.getAllActivitySectors().stream()
            .map(sector -> toResponseDTO(sector))
            .toList();
        return ResponseEntity.ok(sectors);
    }
}
