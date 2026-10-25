package com.credit.bladi.controller.investment;

import static com.credit.bladi.controller.investment.mapper.InvestmentCreditParametersMapper.toDomain;
import static com.credit.bladi.controller.investment.mapper.InvestmentCreditParametersMapper.toResponseDTO;

import com.credit.bladi.common.EnumParsers;
import com.credit.bladi.controller.dto.investment.request.PatchInvestmentCreditParametersDTO;
import com.credit.bladi.controller.dto.investment.response.InvestmentCreditParametersResponseDTO;
import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.service.investment.IInvestmentCreditParametersService;
import com.credit.bladi.validator.ValidationConstraints;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
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
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/investment-credit-parameters")
@PreAuthorize("hasRole('MANAGER')")
public class InvestmentCreditParametersController implements IInvestmentCreditParametersController {
    private final IInvestmentCreditParametersService service;

    public InvestmentCreditParametersController(IInvestmentCreditParametersService service) {
        this.service = service;
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<InvestmentCreditParametersResponseDTO> getById(@PathVariable UUID id) {
        Long resolvedId = service.resolveParametersId(id);
        return ResponseEntity.ok(toResponseDTO(service.getById(resolvedId)));
    }

    @Override
    @GetMapping("/type/{enterpriseType}")
    public ResponseEntity<InvestmentCreditParametersResponseDTO> getActiveByEnterpriseType(
            @Pattern(regexp = ValidationConstraints.ENTERPRISE_TYPE_REGEX,
                message = "{validation.investmentCredit.enterpriseType.pattern}")
            @PathVariable String enterpriseType) {
        EnterpriseType parsedType = EnumParsers.parse(EnterpriseType.class, enterpriseType, "enterpriseType");
        return ResponseEntity.ok(toResponseDTO(service.getActiveByEnterpriseType(parsedType)));
    }

    @Override
    @PatchMapping("/{id}")
    public ResponseEntity<Void> patchParameters(
            @PathVariable UUID id,
            @Valid @RequestBody PatchInvestmentCreditParametersDTO dto) {
        Long resolvedId = service.resolveParametersId(id);
        service.patchParameters(toDomain(resolvedId, dto));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
