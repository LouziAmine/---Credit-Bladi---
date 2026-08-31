package com.credit.bladi.controller.creditrate;

import static com.credit.bladi.controller.creditrate.mapper.CreditRateMapper.toCreditPage;
import static com.credit.bladi.controller.creditrate.mapper.CreditRateMapper.toDomain;
import static com.credit.bladi.controller.creditrate.mapper.CreditRateMapper.toResponseDTO;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.controller.dto.creditrate.request.PatchCreditRateDTO;
import com.credit.bladi.controller.dto.creditrate.response.CreditRateResponseDTO;
import com.credit.bladi.service.creditrate.ICreditRateService;
import jakarta.validation.Valid;
import java.util.UUID;
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
@RequestMapping("/api/v1/credit-rates")
@PreAuthorize("hasRole('MANAGER')")
public class CreditRateController implements ICreditRateController {
    private final ICreditRateService creditRateService;

    public CreditRateController(ICreditRateService creditRateService) {
        this.creditRateService = creditRateService;
    }

    @Override
    @GetMapping
    public ResponseEntity<CreditPage<CreditRateResponseDTO>> getAllCreditRates(
        @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
        @RequestParam(name = "size", defaultValue = "20", required = false) Integer size
    ) {
        return ResponseEntity.ok(toCreditPage(creditRateService.getAllParameters(page, size)));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<CreditRateResponseDTO> getById(@PathVariable UUID id) {
        Long resolvedId = creditRateService.resolveCreditRateId(id);
        return ResponseEntity.ok(toResponseDTO(creditRateService.getById(resolvedId)));
    }

    @Override
    @PatchMapping("/{id}")
    public ResponseEntity<Void> patchCreditRate(
        @PathVariable UUID id,
        @Valid @RequestBody PatchCreditRateDTO dto
    ) {
        Long resolvedId = creditRateService.resolveCreditRateId(id);
        creditRateService.patchParameters(toDomain(resolvedId, dto));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
