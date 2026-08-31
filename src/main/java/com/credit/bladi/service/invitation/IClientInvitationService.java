package com.credit.bladi.service.invitation;

import com.credit.bladi.model.enums.EnterpriseLinkRole;
import com.credit.bladi.model.enums.Language;
import com.credit.bladi.model.invitation.ClientInvitationPreview;

public interface IClientInvitationService {
    void createInvitation(Long enterpriseId, String email, Language language, EnterpriseLinkRole role,
            Long demandId, String createdByUsername);

    void createBorrowerInvitation(Long demandId, String email, Language language, String createdByUsername);

    ClientInvitationPreview preview(String rawToken);

    void accept(String rawToken, String username, String password);
}
