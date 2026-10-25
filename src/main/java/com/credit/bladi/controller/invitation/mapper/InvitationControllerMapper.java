package com.credit.bladi.controller.invitation.mapper;

import com.credit.bladi.controller.dto.invitation.response.ClientInvitationPreviewResponseDTO;
import com.credit.bladi.model.invitation.ClientInvitationPreview;

public final class InvitationControllerMapper {
    private InvitationControllerMapper() {
    }

    public static ClientInvitationPreviewResponseDTO toResponseDTO(ClientInvitationPreview preview) {
        return new ClientInvitationPreviewResponseDTO(preview.email(), preview.enterpriseName(),
            preview.demandReference(), preview.expiresAt());
    }
}
