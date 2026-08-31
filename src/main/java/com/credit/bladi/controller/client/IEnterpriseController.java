package com.credit.bladi.controller.client;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.controller.dto.client.request.CreateEnterpriseRequestDTO;
import com.credit.bladi.controller.dto.client.response.EnterpriseMatchResponseDTO;
import com.credit.bladi.controller.dto.client.response.EnterpriseResponseDTO;
import com.credit.bladi.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Enterprises", description = "Client companies (Enterprises) and their credit file portfolio. "
    + "Manager use only.")
public interface IEnterpriseController {
    @Operation(summary = "Create a new Enterprise (client company)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Enterprise created",
            content = @Content(schema = @Schema(implementation = EnterpriseResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request payload",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<EnterpriseResponseDTO> createEnterprise(@Valid @RequestBody CreateEnterpriseRequestDTO requestDTO);

    @Operation(summary = "List Enterprises (paginated)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Paginated list of Enterprises",
            content = @Content(schema = @Schema(implementation = CreditPage.class)))
    })
    ResponseEntity<CreditPage<EnterpriseResponseDTO>> getAllEnterprises(
        @Parameter(description = "Page number (1-based)", name = "page", example = "1")
        @Min(value = 1, message = "{validation.invalidPage}")
        @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
        @Parameter(description = "Page size", name = "size", example = "20")
        @Min(value = 1, message = "{validation.invalidSize.min}")
        @RequestParam(name = "size", defaultValue = "20", required = false) Integer size
    );

    @Operation(summary = "Get an Enterprise by id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "The Enterprise",
            content = @Content(schema = @Schema(implementation = EnterpriseResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "No Enterprise found for the given ID",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<EnterpriseResponseDTO> getEnterpriseById(
        @Parameter(description = "Identifier of the Enterprise.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @PathVariable UUID id
    );

    @Operation(summary = "Look up an Enterprise by a free-text company name",
        description = "Compares the given name against every Enterprise using a normalized comparison "
            + "(uppercased, accents/punctuation stripped, common legal-form suffixes like SARL/SA/SAS "
            + "dropped) rather than an exact string match: used to detect when a demand's free-text "
            + "companyName (as typed into the public simulator) already corresponds to an onboarded "
            + "Enterprise before classifying a demand as REAL. Always returns 200; matched=false means "
            + "no existing Enterprise was found, not an error.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Match result (matched may be false)",
            content = @Content(schema = @Schema(implementation = EnterpriseMatchResponseDTO.class)))
    })
    ResponseEntity<EnterpriseMatchResponseDTO> matchEnterpriseByName(
        @Parameter(description = "Free-text company name to look up.", example = "AFD Technology")
        @NotBlank(message = "{validation.invalidName.notBlank}")
        @RequestParam(name = "name") String name
    );
}
