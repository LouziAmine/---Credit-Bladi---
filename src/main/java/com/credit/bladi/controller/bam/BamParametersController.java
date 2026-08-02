package com.credit.bladi.controller.bam;

import static com.credit.bladi.controller.bam.mapper.BamParametersMapper.toCreditPage;
import static com.credit.bladi.controller.bam.mapper.BamParametersMapper.toDomain;
import static com.credit.bladi.controller.bam.mapper.BamParametersMapper.toResponseDTO;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.controller.dto.bam.request.PatchBamParametersDTO;
import com.credit.bladi.controller.dto.bam.response.BamParametersResponseDTO;
import com.credit.bladi.service.bamparameters.IBamParametersService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/bam-parameters")
@PreAuthorize("hasRole('MANAGER')")
public class BamParametersController implements IBamParametersController {

    private final IBamParametersService bamParametersService;

    public BamParametersController(IBamParametersService bamParametersService) {
        this.bamParametersService = bamParametersService;
    }

    @Override
    @GetMapping
    public ResponseEntity<CreditPage<BamParametersResponseDTO>> getAllParameters(
        @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
        @RequestParam(name = "size", defaultValue = "20", required = false) Integer size
    ) {
        return ResponseEntity.ok(toCreditPage(bamParametersService.getAllParameters(page, size)));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<BamParametersResponseDTO> getById(
            @Min(value = 1, message = "INVALID_ID: id must be a positive number.") @PathVariable Long id) {
        return ResponseEntity.ok(toResponseDTO(bamParametersService.getById(id)));
    }

    @Override
    @PatchMapping("/{id}")
    public ResponseEntity<Void> patchParameters(
        @Min(value = 1, message = "INVALID_ID: id must be a positive number.") @PathVariable Long id,
        @Valid @RequestBody PatchBamParametersDTO dto
    ) {
        bamParametersService.patchParameters(toDomain(id, dto));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}