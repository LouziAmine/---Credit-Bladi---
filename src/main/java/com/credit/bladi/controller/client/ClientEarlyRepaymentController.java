package com.credit.bladi.controller.client;

import static com.credit.bladi.controller.servicing.mapper.EarlyRepaymentMapper.toPrincipal;
import static com.credit.bladi.controller.servicing.mapper.EarlyRepaymentMapper.toResponseDTO;

import com.credit.bladi.controller.dto.servicing.response.EarlyRepaymentQuoteResponseDTO;
import com.credit.bladi.service.security.ClientDemandAccessGuard;
import com.credit.bladi.service.servicing.IEarlyRepaymentService;
import com.credit.bladi.service.workflow.engine.IWorkflowEngineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/me/demands/{demandId}/credit-file/early-repayments")
@PreAuthorize("hasRole('CLIENT')")
@Tag(name = "Client Early Repayment", description = "Client-side quote of a remboursement anticipé.")
public class ClientEarlyRepaymentController {
    private final IEarlyRepaymentService earlyRepaymentService;

    private final IWorkflowEngineService workflowEngineService;

    private final ClientDemandAccessGuard accessGuard;

    public ClientEarlyRepaymentController(IEarlyRepaymentService earlyRepaymentService,
            IWorkflowEngineService workflowEngineService, ClientDemandAccessGuard accessGuard) {
        this.earlyRepaymentService = earlyRepaymentService;
        this.workflowEngineService = workflowEngineService;
        this.accessGuard = accessGuard;
    }

    @Operation(summary = "Quote an early repayment of my credit made today")
    @GetMapping("/quote")
    public ResponseEntity<EarlyRepaymentQuoteResponseDTO> quote(
            @Parameter(description = "Identifier of the demand.") @PathVariable UUID demandId,
            @Parameter(description = "Capital to repay (MAD); omit for a total repayment.")
            @DecimalMin(value = "0.01", message = "{validation.earlyRepayment.amount.min}")
            @RequestParam(name = "amount", required = false) BigDecimal amount) {
        Long resolvedDemandId = workflowEngineService.resolveDemandId(demandId);
        accessGuard.assertApprovedForDemand(resolvedDemandId);
        return ResponseEntity.ok(toResponseDTO(earlyRepaymentService.quote(resolvedDemandId, toPrincipal(amount))));
    }
}
