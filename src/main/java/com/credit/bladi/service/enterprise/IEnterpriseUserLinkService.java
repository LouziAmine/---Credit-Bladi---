package com.credit.bladi.service.enterprise;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.model.enterprise.EnterpriseUserLinkModel;
import com.credit.bladi.model.enums.EnterpriseLinkRole;
import java.util.List;
import java.util.UUID;

public interface IEnterpriseUserLinkService {
    EnterpriseUserLinkModel inviteClient(Long enterpriseId, String username, EnterpriseLinkRole role);

    Long resolveLinkId(UUID publicId);

    EnterpriseUserLinkModel claimEnterprise(String registrationNumber, EnterpriseLinkRole role);

    EnterpriseUserLinkModel approve(Long linkId);

    EnterpriseUserLinkModel reject(Long linkId, String reason);

    EnterpriseUserLinkModel revoke(Long linkId);

    CreditPage<EnterpriseUserLinkModel> listByEnterprise(Long enterpriseId, int page, int size);

    CreditPage<EnterpriseUserLinkModel> listPending(int page, int size);

    List<EnterpriseUserLinkModel> listMyLinks();

    List<EnterpriseUserLinkModel> listMyApprovedLinks();

    void assertCurrentUserApprovedForEnterprise(Long enterpriseId);
}
