package com.credit.bladi.controller.verification;

import com.credit.bladi.controller.dto.verification.request.BamVerificationRequestDTO;
import com.credit.bladi.controller.dto.verification.response.BamVerificationResponseDTO;
import com.credit.bladi.controller.verification.mapper.BamVerificationMapper;
import com.credit.bladi.model.enums.BamScenario;
import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.model.verification.BamVerificationResult;
import com.credit.bladi.service.verification.IBamVerificationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/mock/verification-bam")
@PreAuthorize("hasRole('MANAGER')")
public class BamVerificationController implements IBamVerificationController {
    private final IBamVerificationService bamVerificationService;

    private final BamVerificationMapper bamVerificationMapper;

    public BamVerificationController(IBamVerificationService bamVerificationService,
            BamVerificationMapper bamVerificationMapper) {
        this.bamVerificationService = bamVerificationService;
        this.bamVerificationMapper = bamVerificationMapper;
    }

    @Override
    @PostMapping
    public ResponseEntity<BamVerificationResponseDTO> verify(
            @Valid @RequestBody BamVerificationRequestDTO requestDTO,
            @RequestParam(required = false) String scenario) {
        EnterpriseType enterpriseType = BamVerificationMapper.toEnterpriseType(requestDTO.enterpriseType());
        BamScenario forcedScenario = BamVerificationMapper.toScenario(scenario);

        BamVerificationResult result = bamVerificationService.verify(enterpriseType, requestDTO.identifier(),
            requestDTO.companyName(), forcedScenario);

        return ResponseEntity.ok(bamVerificationMapper.toResponseDTO(result));
    }
}
