package com.credit.bladi.controller.servicing;

import static com.credit.bladi.controller.servicing.mapper.EarlyRepaymentMapper.toPrincipal;
import static com.credit.bladi.controller.servicing.mapper.EarlyRepaymentMapper.toResponseDTO;

import com.credit.bladi.controller.dto.servicing.request.EarlyRepaymentRequestDTO;
import com.credit.bladi.controller.dto.servicing.response.EarlyRepaymentQuoteResponseDTO;
import com.credit.bladi.controller.dto.servicing.response.EarlyRepaymentResponseDTO;
import com.credit.bladi.controller.servicing.mapper.EarlyRepaymentMapper;
import com.credit.bladi.service.servicing.IEarlyRepaymentService;
import com.credit.bladi.service.workflow.engine.IWorkflowEngineService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/demands/{demandId}/credit-file/early-repayments")
@PreAuthorize("hasRole('MANAGER')")
public class EarlyRepaymentController implements IEarlyRepaymentController {
    private final IEarlyRepaymentService earlyRepaymentService;

    private final IWorkflowEngineService workflowEngineService;

    public EarlyRepaymentController(IEarlyRepaymentService earlyRepaymentService,
            IWorkflowEngineService workflowEngineService) {
        this.earlyRepaymentService = earlyRepaymentService;
        this.workflowEngineService = workflowEngineService;
    }

    @Override
    @GetMapping("/quote")
    public ResponseEntity<EarlyRepaymentQuoteResponseDTO> quote(@PathVariable UUID demandId,
            @RequestParam(name = "amount", required = false) BigDecimal amount) {
        Long resolvedDemandId = workflowEngineService.resolveDemandId(demandId);
        return ResponseEntity.ok(toResponseDTO(earlyRepaymentService.quote(resolvedDemandId, toPrincipal(amount))));
    }

    @Override
    @PostMapping
    public ResponseEntity<EarlyRepaymentResponseDTO> repay(@PathVariable UUID demandId,
            @Valid @RequestBody EarlyRepaymentRequestDTO request) {
        Long resolvedDemandId = workflowEngineService.resolveDemandId(demandId);
        return ResponseEntity.ok(toResponseDTO(
            earlyRepaymentService.repay(resolvedDemandId, toPrincipal(request.amount()))));
    }

    @Override
    @GetMapping
    public ResponseEntity<List<EarlyRepaymentResponseDTO>> history(@PathVariable UUID demandId) {
        Long resolvedDemandId = workflowEngineService.resolveDemandId(demandId);
        return ResponseEntity.ok(earlyRepaymentService.history(resolvedDemandId).stream()
            .map(EarlyRepaymentMapper::toResponseDTO)
            .toList());
    }
}
