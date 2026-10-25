package com.credit.bladi.controller.sector;

import com.credit.bladi.controller.dto.sector.request.CreateActivitySectorRequestDTO;
import com.credit.bladi.controller.dto.sector.response.ActivitySectorResponseDTO;
import com.credit.bladi.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Activity Sectors", description = "Shared, manager-addable list of business sectors ('Activity "
    + "Sector') used on the public simulation forms and the Enterprise creation form.")
public interface IActivitySectorController {
    @Operation(summary = "Add a new Activity Sector (manager use only)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Activity sector created",
            content = @Content(schema = @Schema(implementation = ActivitySectorResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request payload",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "An activity sector with this name already exists",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<ActivitySectorResponseDTO> createActivitySector(
        @Valid @RequestBody CreateActivitySectorRequestDTO requestDTO);

    @Operation(summary = "List all Activity Sectors, alphabetically. Public: used to populate simulation forms.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "All activity sectors",
            content = @Content(schema = @Schema(implementation = ActivitySectorResponseDTO.class)))
    })
    ResponseEntity<List<ActivitySectorResponseDTO>> getAllActivitySectors();
}
