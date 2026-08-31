package com.credit.bladi.controller.client;

import static com.credit.bladi.controller.client.mapper.ClientPortfolioControllerMapper.toDemandSummaryCreditPage;
import static com.credit.bladi.controller.client.mapper.ClientPortfolioControllerMapper.toPortfolioEntryDTO;
import static com.credit.bladi.controller.enterprise.mapper.EnterpriseUserLinkControllerMapper.toResponseDTO;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.controller.client.mapper.ClientPortfolioControllerMapper;
import com.credit.bladi.controller.dto.enterprise.request.ClaimEnterpriseLinkRequestDTO;
import com.credit.bladi.controller.dto.enterprise.response.ClientDemandSummaryResponseDTO;
import com.credit.bladi.controller.dto.enterprise.response.ClientPortfolioEnterpriseResponseDTO;
import com.credit.bladi.controller.dto.enterprise.response.EnterpriseUserLinkResponseDTO;
import com.credit.bladi.controller.enterprise.mapper.EnterpriseUserLinkControllerMapper;
import com.credit.bladi.model.client.EnterpriseModel;
import com.credit.bladi.model.enterprise.EnterpriseUserLinkModel;
import com.credit.bladi.model.enums.EnterpriseLinkRole;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.service.borrower.IMortgageBorrowerService;
import com.credit.bladi.service.client.IEnterpriseService;
import com.credit.bladi.service.enterprise.IEnterpriseUserLinkService;
import com.credit.bladi.service.workflow.engine.IWorkflowEngineService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
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
@RequestMapping(ClientPortfolioController.BASE_PATH)
@PreAuthorize("hasRole('CLIENT')")
public class ClientPortfolioController implements IClientPortfolioController {
    static final String BASE_PATH = "/api/v1/me";

    private static final String ENTERPRISE_LINKS_PATH = "/enterprise-links";

    private final IEnterpriseUserLinkService linkService;

    private final IEnterpriseService enterpriseService;

    private final IWorkflowEngineService workflowEngineService;

    private final IMortgageBorrowerService borrowerService;

    public ClientPortfolioController(IEnterpriseUserLinkService linkService, IEnterpriseService enterpriseService,
            IWorkflowEngineService workflowEngineService, IMortgageBorrowerService borrowerService) {
        this.linkService = linkService;
        this.enterpriseService = enterpriseService;
        this.workflowEngineService = workflowEngineService;
        this.borrowerService = borrowerService;
    }

    @Override
    @PostMapping(ENTERPRISE_LINKS_PATH)
    public ResponseEntity<EnterpriseUserLinkResponseDTO> claimEnterprise(
            @Valid @RequestBody ClaimEnterpriseLinkRequestDTO requestDTO) {
        EnterpriseUserLinkModel created = linkService.claimEnterprise(requestDTO.registrationNumber(),
            EnterpriseLinkRole.valueOf(requestDTO.role()));
        return ResponseEntity.created(URI.create(BASE_PATH + ENTERPRISE_LINKS_PATH + "/" + created.publicId()))
            .body(toResponseDTO(created));
    }

    @Override
    @GetMapping(ENTERPRISE_LINKS_PATH)
    public ResponseEntity<List<EnterpriseUserLinkResponseDTO>> getMyEnterpriseLinks() {
        return ResponseEntity.ok(linkService.listMyLinks().stream()
            .map(EnterpriseUserLinkControllerMapper::toResponseDTO)
            .toList());
    }

    @Override
    @GetMapping("/portfolio")
    public ResponseEntity<List<ClientPortfolioEnterpriseResponseDTO>> getMyPortfolio() {
        List<EnterpriseUserLinkModel> approvedLinks = linkService.listMyApprovedLinks();
        Map<Long, EnterpriseModel> enterprisesById = enterpriseService
            .getEnterprisesByIds(approvedLinks.stream().map(EnterpriseUserLinkModel::enterpriseId).distinct().toList())
            .stream()
            .collect(Collectors.toMap(EnterpriseModel::id, Function.identity()));
        List<ClientPortfolioEnterpriseResponseDTO> portfolio = approvedLinks.stream()
            .map(link -> toPortfolioEntryDTO(enterprisesById.get(link.enterpriseId()), link))
            .toList();
        return ResponseEntity.ok(portfolio);
    }

    @Override
    @GetMapping("/portfolio/{enterpriseId}/demands")
    public ResponseEntity<CreditPage<ClientDemandSummaryResponseDTO>> getPortfolioEnterpriseDemands(
            @PathVariable UUID enterpriseId,
            @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "20", required = false) Integer size) {
        Long resolvedEnterpriseId = enterpriseService.resolveEnterpriseId(enterpriseId);
        linkService.assertCurrentUserApprovedForEnterprise(resolvedEnterpriseId);
        return ResponseEntity.ok(toDemandSummaryCreditPage(
            workflowEngineService.getAllDemands(page, size, enterpriseId, null, null, null, null)));
    }

    @Override
    @GetMapping("/mortgage-demands")
    public ResponseEntity<List<ClientDemandSummaryResponseDTO>> getMyMortgageDemands() {
        List<Long> demandIds = borrowerService.getMyDemandIds();
        Map<Long, DemandModel> demandsById = workflowEngineService.getDemands(demandIds);
        return ResponseEntity.ok(demandIds.stream()
            .map(demandsById::get)
            .map(ClientPortfolioControllerMapper::toDemandSummaryDTO)
            .toList());
    }
}
