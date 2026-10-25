package com.credit.bladi.controller.client;

import static com.credit.bladi.controller.servicing.mapper.CreditFileMapper.toResponseDTO;

import com.credit.bladi.controller.dto.servicing.request.DeclarePaymentRequestDTO;
import com.credit.bladi.controller.dto.servicing.response.CreditFileResponseDTO;
import com.credit.bladi.controller.dto.servicing.response.InstallmentResponseDTO;
import com.credit.bladi.model.servicing.CreditFileModel;
import com.credit.bladi.model.servicing.PaymentBeneficiary;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.service.client.IEnterpriseService;
import com.credit.bladi.service.security.ClientDemandAccessGuard;
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
@RequestMapping("/api/v1/me/demands/{demandId}/credit-file")
@PreAuthorize("hasRole('CLIENT')")
public class ClientCreditFileController implements IClientCreditFileController {
    private final ICreditFileService creditFileService;

    private final IWorkflowEngineService workflowEngineService;

    private final ClientDemandAccessGuard accessGuard;

    private final IEnterpriseService enterpriseService;

    private final PaymentBeneficiary beneficiary;

    public ClientCreditFileController(ICreditFileService creditFileService,
            IWorkflowEngineService workflowEngineService, ClientDemandAccessGuard accessGuard,
            IEnterpriseService enterpriseService, PaymentBeneficiary beneficiary) {
        this.creditFileService = creditFileService;
        this.workflowEngineService = workflowEngineService;
        this.accessGuard = accessGuard;
        this.enterpriseService = enterpriseService;
        this.beneficiary = beneficiary;
    }

    @Override
    @GetMapping
    public ResponseEntity<CreditFileResponseDTO> getByDemandId(@PathVariable UUID demandId) {
        Long resolvedDemandId = workflowEngineService.resolveDemandId(demandId);
        accessGuard.assertApprovedForDemand(resolvedDemandId);
        CreditFileModel creditFile = creditFileService.getByDemandId(resolvedDemandId);
        DemandModel demand = workflowEngineService.getDemand(resolvedDemandId);
        String clientRib = enterpriseService.getRibOrNull(demand.enterpriseIdOrNull());
        return ResponseEntity.ok(toResponseDTO(creditFile, demand,
            creditFileService.getInstallments(creditFile.id()), beneficiary, clientRib));
    }

    @Override
    @PostMapping("/installments/{installmentId}/payments")
    public ResponseEntity<InstallmentResponseDTO> payInstallment(
            @PathVariable UUID demandId,
            @PathVariable UUID installmentId,
            @Valid @RequestBody DeclarePaymentRequestDTO request) {
        Long resolvedDemandId = workflowEngineService.resolveDemandId(demandId);
        accessGuard.assertApprovedForDemand(resolvedDemandId);
        Long resolvedInstallmentId = creditFileService.resolveInstallmentId(installmentId);
        InstallmentResponseDTO response = toResponseDTO(
            creditFileService.declarePayment(resolvedDemandId, resolvedInstallmentId, request.sourceRib()));
        return ResponseEntity.ok(response);
    }
}
