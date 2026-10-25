package com.credit.bladi.controller.reference;

import com.credit.bladi.controller.dto.reference.response.GradeResponseDTO;
import com.credit.bladi.controller.dto.reference.response.TeamResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;

@Tag(name = "Reference Data", description = "Read-only lookup lists (grades, teams) used to profile users "
    + "and to drive workflow step routing.")
public interface IReferenceDataController {
    @Operation(summary = "List all grades")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "All grades",
            content = @Content(schema = @Schema(implementation = GradeResponseDTO.class)))
    })
    ResponseEntity<List<GradeResponseDTO>> getGrades();

    @Operation(summary = "List all teams")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "All teams",
            content = @Content(schema = @Schema(implementation = TeamResponseDTO.class)))
    })
    ResponseEntity<List<TeamResponseDTO>> getTeams();
}
