package com.credit.bladi.controller.servicing;

import static com.credit.bladi.controller.servicing.mapper.CreditFileMapper.toResponseDTO;

import com.credit.bladi.controller.dto.servicing.request.RejectPaymentRequestDTO;
import com.credit.bladi.controller.dto.servicing.response.CreditFileResponseDTO;
import com.credit.bladi.controller.dto.servicing.response.InstallmentResponseDTO;
import com.credit.bladi.model.servicing.CreditFileModel;
import com.credit.bladi.model.servicing.PaymentBeneficiary;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.service.client.IEnterpriseService;
import com.credit.bladi.service.servicing.ICreditFileService;
import com.credit.bladi.service.workflow.engine.IWorkflowEngineService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/demands/{demandId}/credit-file")
@PreAuthorize("hasRole('MANAGER')")
public class CreditFileController implements ICreditFileController {
    private final ICreditFileService creditFileService;

    private final IWorkflowEngineService workflowEngineService;

    private final IEnterpriseService enterpriseService;

    private final PaymentBeneficiary beneficiary;

    public CreditFileController(ICreditFileService creditFileService, IWorkflowEngineService workflowEngineService,
            IEnterpriseService enterpriseService, PaymentBeneficiary beneficiary) {
        this.creditFileService = creditFileService;
        this.workflowEngineService = workflowEngineService;
        this.enterpriseService = enterpriseService;
        this.beneficiary = beneficiary;
    }

    @Override
    @GetMapping
    public ResponseEntity<CreditFileResponseDTO> getByDemandId(@PathVariable UUID demandId) {
        Long resolvedDemandId = workflowEngineService.resolveDemandId(demandId);
        CreditFileModel creditFile = creditFileService.getByDemandId(resolvedDemandId);
        DemandModel demand = workflowEngineService.getDemand(resolvedDemandId);
        String clientRib = enterpriseService.getRibOrNull(demand.enterpriseIdOrNull());
        return ResponseEntity.ok(toResponseDTO(creditFile, demand,
            creditFileService.getInstallments(creditFile.id()), beneficiary, clientRib));
    }

    @Override
    @PostMapping("/installments/{installmentId}/payments/validate")
    public ResponseEntity<InstallmentResponseDTO> validatePayment(
            @PathVariable UUID demandId,
            @PathVariable UUID installmentId) {
        Long resolvedDemandId = workflowEngineService.resolveDemandId(demandId);
        Long resolvedInstallmentId = creditFileService.resolveInstallmentId(installmentId);
        InstallmentResponseDTO response = toResponseDTO(
            creditFileService.validatePayment(resolvedDemandId, resolvedInstallmentId));
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/installments/{installmentId}/payments/reject")
    public ResponseEntity<InstallmentResponseDTO> rejectPayment(
            @PathVariable UUID demandId,
            @PathVariable UUID installmentId,
            @Valid @RequestBody RejectPaymentRequestDTO request) {
        Long resolvedDemandId = workflowEngineService.resolveDemandId(demandId);
        Long resolvedInstallmentId = creditFileService.resolveInstallmentId(installmentId);
        InstallmentResponseDTO response = toResponseDTO(
            creditFileService.rejectPayment(resolvedDemandId, resolvedInstallmentId, request.reason()));
        return ResponseEntity.ok(response);
    }
}
