package com.credit.bladi.model.invitation;

import java.time.Instant;

public record ClientInvitationPreview(String email, String enterpriseName, String demandReference, Instant expiresAt) {
}
