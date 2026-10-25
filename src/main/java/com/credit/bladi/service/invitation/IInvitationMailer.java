package com.credit.bladi.service.invitation;

import com.credit.bladi.model.enums.Language;

public interface IInvitationMailer {
    void sendInvitation(String toEmail, String enterpriseName, String rawToken, Language language);

    void sendAccountConfirmation(String toEmail, String username, String enterpriseName, Language language);

    void sendBorrowerInvitation(String toEmail, String demandReference, String rawToken, Language language);

    void sendBorrowerAccountConfirmation(String toEmail, String username, String demandReference, Language language);
}
