package com.credit.bladi.unittest.controller.enterprise;

import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_APPROVED;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_REJECTED;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_RESPONSABLE;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_REVOKED;
import static com.credit.bladi.unittest.mock.data.MockTokens.TOKEN_INVALIDE;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_JDUPONT;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_MANAGER1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.controller.dto.enterprise.request.InviteEnterpriseLinkRequestDTO;
import com.credit.bladi.controller.dto.enterprise.request.RejectEnterpriseLinkRequestDTO;
import com.credit.bladi.controller.dto.enterprise.response.EnterpriseUserLinkResponseDTO;
import com.credit.bladi.controller.enterprise.EnterpriseUserLinkController;
import com.credit.bladi.model.enterprise.EnterpriseUserLinkModel;
import com.credit.bladi.model.enums.EnterpriseLinkOrigin;
import com.credit.bladi.model.enums.EnterpriseLinkRole;
import com.credit.bladi.model.enums.EnterpriseLinkStatus;
import com.credit.bladi.service.client.IEnterpriseService;
import com.credit.bladi.service.enterprise.IEnterpriseUserLinkService;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class EnterpriseUserLinkControllerTest {
    private static final UUID ENTERPRISE_PUBLIC_ID = UUID.randomUUID();

    private static final UUID LINK_PUBLIC_ID = UUID.randomUUID();

    @Mock
    private IEnterpriseUserLinkService linkService;

    @Mock
    private IEnterpriseService enterpriseService;

    private EnterpriseUserLinkController controller;

    @BeforeEach
    void setUp() {
        controller = new EnterpriseUserLinkController(linkService, enterpriseService);
    }

    private static EnterpriseUserLinkModel link(EnterpriseLinkStatus status) {
        return new EnterpriseUserLinkModel(10L, LINK_PUBLIC_ID, 1L, null, 5L, null, EnterpriseLinkRole.RESPONSABLE,
            status,
            EnterpriseLinkOrigin.MANAGER_INVITE, null, USER_MANAGER1, null, null, Instant.now(), null);
    }

    @Test
    void shouldReturn201WhenInvitingClient() {
        InviteEnterpriseLinkRequestDTO request = new InviteEnterpriseLinkRequestDTO(USER_JDUPONT, CODE_RESPONSABLE);
        when(enterpriseService.resolveEnterpriseId(ENTERPRISE_PUBLIC_ID)).thenReturn(1L);
        when(linkService.inviteClient(1L, USER_JDUPONT, EnterpriseLinkRole.RESPONSABLE))
            .thenReturn(link(EnterpriseLinkStatus.APPROVED));

        ResponseEntity<EnterpriseUserLinkResponseDTO> result = controller.inviteClient(ENTERPRISE_PUBLIC_ID, request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(CODE_APPROVED, result.getBody().status());
    }

    @Test
    void shouldReturnPendingLinksPage() {
        CreditPage<EnterpriseUserLinkModel> page = new CreditPage<>();
        page.setItems(java.util.List.of(link(EnterpriseLinkStatus.PENDING)));
        when(linkService.listPending(1, 20)).thenReturn(page);

        ResponseEntity<CreditPage<EnterpriseUserLinkResponseDTO>> result = controller.getPendingLinks(1, 20);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().getItems().size());
    }

    @Test
    void shouldApproveLink() {
        when(linkService.resolveLinkId(LINK_PUBLIC_ID)).thenReturn(10L);
        when(linkService.approve(10L)).thenReturn(link(EnterpriseLinkStatus.APPROVED));

        ResponseEntity<EnterpriseUserLinkResponseDTO> result = controller.approveLink(LINK_PUBLIC_ID);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(CODE_APPROVED, result.getBody().status());
    }

    @Test
    void shouldRejectLinkWithReason() {
        RejectEnterpriseLinkRequestDTO request = new RejectEnterpriseLinkRequestDTO(TOKEN_INVALIDE);
        when(linkService.resolveLinkId(LINK_PUBLIC_ID)).thenReturn(10L);
        when(linkService.reject(eq(10L), eq(TOKEN_INVALIDE))).thenReturn(link(EnterpriseLinkStatus.REJECTED));

        ResponseEntity<EnterpriseUserLinkResponseDTO> result = controller.rejectLink(LINK_PUBLIC_ID, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(CODE_REJECTED, result.getBody().status());
    }

    @Test
    void shouldRevokeLink() {
        when(linkService.resolveLinkId(LINK_PUBLIC_ID)).thenReturn(10L);
        when(linkService.revoke(10L)).thenReturn(link(EnterpriseLinkStatus.REVOKED));

        ResponseEntity<EnterpriseUserLinkResponseDTO> result = controller.revokeLink(LINK_PUBLIC_ID);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(CODE_REVOKED, result.getBody().status());
    }
}
