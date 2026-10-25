package com.credit.bladi.repository.enterprise;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.model.enterprise.EnterpriseUserLinkModel;
import com.credit.bladi.model.enums.EnterpriseLinkOrigin;
import com.credit.bladi.model.enums.EnterpriseLinkRole;
import com.credit.bladi.model.enums.EnterpriseLinkStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IEnterpriseUserLinkRepository {
    EnterpriseUserLinkModel create(Long enterpriseId, Long userId, EnterpriseLinkRole role,
            EnterpriseLinkStatus status, EnterpriseLinkOrigin origin, String claimedRegistrationNumber,
            String requestedByUsername);

    Optional<EnterpriseUserLinkModel> findById(Long id);

    Optional<Long> findIdByPublicId(UUID publicId);

    Optional<EnterpriseUserLinkModel> findActiveLink(Long enterpriseId, Long userId);

    CreditPage<EnterpriseUserLinkModel> findByEnterpriseId(Long enterpriseId, int page, int size);

    CreditPage<EnterpriseUserLinkModel> findByStatus(EnterpriseLinkStatus status, int page, int size);

    List<EnterpriseUserLinkModel> findByUserId(Long userId);

    List<EnterpriseUserLinkModel> findApprovedByUserId(Long userId);

    List<EnterpriseUserLinkModel> findApprovedByEnterpriseId(Long enterpriseId);

    EnterpriseUserLinkModel updateStatus(Long id, EnterpriseLinkStatus status, String decidedByUsername,
            Instant decidedAt);

    EnterpriseUserLinkModel decideIfPending(Long id, EnterpriseLinkStatus status, String decidedByUsername,
            Instant decidedAt);
}
