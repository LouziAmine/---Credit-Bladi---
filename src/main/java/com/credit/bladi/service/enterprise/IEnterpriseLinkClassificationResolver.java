package com.credit.bladi.service.enterprise;

import com.credit.bladi.model.enums.Language;

public interface IEnterpriseLinkClassificationResolver {
    void resolveClientForClassification(Long enterpriseId, String contactEmail, Language contactLanguage,
            Long demandId, String actorUsername);
}
