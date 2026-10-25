package com.credit.bladi.controller.enterprise;

import static com.credit.bladi.controller.enterprise.mapper.EnterpriseUserLinkControllerMapper.toCreditPage;
import static com.credit.bladi.controller.enterprise.mapper.EnterpriseUserLinkControllerMapper.toResponseDTO;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.controller.dto.enterprise.request.InviteEnterpriseLinkRequestDTO;
import com.credit.bladi.controller.dto.enterprise.request.RejectEnterpriseLinkRequestDTO;
import com.credit.bladi.controller.dto.enterprise.response.EnterpriseUserLinkResponseDTO;
import com.credit.bladi.model.enterprise.EnterpriseUserLinkModel;
import com.credit.bladi.model.enums.EnterpriseLinkRole;
import com.credit.bladi.service.client.IEnterpriseService;
import com.credit.bladi.service.enterprise.IEnterpriseUserLinkService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(EnterpriseUserLinkController.BASE_PATH)
@PreAuthorize("hasRole('MANAGER')")
public class EnterpriseUserLinkController implements IEnterpriseUserLinkController {
    static final String BASE_PATH = "/api/v1";

    private static final String USER_LINKS_PATH = "/enterprise-user-links";

    private final IEnterpriseUserLinkService linkService;

    private final IEnterpriseService enterpriseService;

    public EnterpriseUserLinkController(IEnterpriseUserLinkService linkService, IEnterpriseService enterpriseService) {
        this.linkService = linkService;
        this.enterpriseService = enterpriseService;
    }

    @Override
    @PostMapping("/enterprises/{enterpriseId}/user-links")
    public ResponseEntity<EnterpriseUserLinkResponseDTO> inviteClient(
            @PathVariable UUID enterpriseId,
            @Valid @RequestBody InviteEnterpriseLinkRequestDTO requestDTO) {
        Long resolvedEnterpriseId = enterpriseService.resolveEnterpriseId(enterpriseId);
        EnterpriseUserLinkModel created = linkService.inviteClient(resolvedEnterpriseId, requestDTO.username(),
            EnterpriseLinkRole.valueOf(requestDTO.role()));
        return ResponseEntity.created(URI.create(BASE_PATH + USER_LINKS_PATH + "/" + created.publicId()))
            .body(toResponseDTO(created));
    }

    @Override
    @GetMapping("/enterprises/{enterpriseId}/user-links")
    public ResponseEntity<CreditPage<EnterpriseUserLinkResponseDTO>> getLinksForEnterprise(
            @PathVariable UUID enterpriseId,
            @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "20", required = false) Integer size) {
        Long resolvedEnterpriseId = enterpriseService.resolveEnterpriseId(enterpriseId);
        return ResponseEntity.ok(toCreditPage(linkService.listByEnterprise(resolvedEnterpriseId, page, size)));
    }

    @Override
    @GetMapping(USER_LINKS_PATH)
    public ResponseEntity<CreditPage<EnterpriseUserLinkResponseDTO>> getPendingLinks(
            @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "20", required = false) Integer size) {
        return ResponseEntity.ok(toCreditPage(linkService.listPending(page, size)));
    }

    @Override
    @PatchMapping(USER_LINKS_PATH + "/{linkId}/approve")
    public ResponseEntity<EnterpriseUserLinkResponseDTO> approveLink(@PathVariable UUID linkId) {
        Long resolvedLinkId = linkService.resolveLinkId(linkId);
        return ResponseEntity.ok(toResponseDTO(linkService.approve(resolvedLinkId)));
    }

    @Override
    @PatchMapping(USER_LINKS_PATH + "/{linkId}/reject")
    public ResponseEntity<EnterpriseUserLinkResponseDTO> rejectLink(
            @PathVariable UUID linkId,
            @Valid @RequestBody RejectEnterpriseLinkRequestDTO requestDTO) {
        Long resolvedLinkId = linkService.resolveLinkId(linkId);
        return ResponseEntity.ok(toResponseDTO(linkService.reject(resolvedLinkId, requestDTO.reason())));
    }

    @Override
    @PatchMapping(USER_LINKS_PATH + "/{linkId}/revoke")
    public ResponseEntity<EnterpriseUserLinkResponseDTO> revokeLink(@PathVariable UUID linkId) {
        Long resolvedLinkId = linkService.resolveLinkId(linkId);
        return ResponseEntity.ok(toResponseDTO(linkService.revoke(resolvedLinkId)));
    }
}
