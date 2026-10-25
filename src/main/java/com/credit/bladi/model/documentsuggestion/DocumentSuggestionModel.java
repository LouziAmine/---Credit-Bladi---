package com.credit.bladi.model.documentsuggestion;

import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.EnterpriseType;
import java.time.Instant;
import java.util.UUID;

public record DocumentSuggestionModel(Long id, UUID publicId, EnterpriseType enterpriseType, CspCode cspCode,
        String label, Instant createdAt) {
}
