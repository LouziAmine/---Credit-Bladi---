package com.credit.bladi.repository.enterprise;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.datasource.enterprise.EnterpriseUserLinkDatasource;
import com.credit.bladi.datasource.enterprise.entity.EnterpriseUserLinkEntity;
import com.credit.bladi.datasource.enterprise.mapper.EnterpriseUserLinkEntityMapper;
import com.credit.bladi.model.enterprise.EnterpriseUserLinkModel;
import com.credit.bladi.model.enums.EnterpriseLinkOrigin;
import com.credit.bladi.model.enums.EnterpriseLinkRole;
import com.credit.bladi.model.enums.EnterpriseLinkStatus;
import com.credit.bladi.repository.DuplicateEntryException;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class EnterpriseUserLinkRepository implements IEnterpriseUserLinkRepository {
    private static final List<EnterpriseLinkStatus> ACTIVE_STATUSES =
        List.of(EnterpriseLinkStatus.PENDING, EnterpriseLinkStatus.APPROVED);

    private final EnterpriseUserLinkDatasource datasource;

    private final Clock clock;

    public EnterpriseUserLinkRepository(EnterpriseUserLinkDatasource datasource, Clock clock) {
        this.clock = clock;
        this.datasource = datasource;
    }

    @Override
    public EnterpriseUserLinkModel create(Long enterpriseId, Long userId, EnterpriseLinkRole role,
            EnterpriseLinkStatus status, EnterpriseLinkOrigin origin, String claimedRegistrationNumber,
            String requestedByUsername) {
        EnterpriseUserLinkEntity entity = EnterpriseUserLinkEntityMapper.mapToNewEntity(enterpriseId, userId, role,
            status, origin, claimedRegistrationNumber, requestedByUsername, clock.instant());
        try {
            datasource.saveAndFlush(entity);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEntryException(e);
        }
        return toModel(entity);
    }

    @Override
    public Optional<EnterpriseUserLinkModel> findById(Long id) {
        return datasource.findById(id).map(this::toModel);
    }

    @Override
    public Optional<Long> findIdByPublicId(UUID publicId) {
        return datasource.findByPublicId(publicId).map(EnterpriseUserLinkEntity::getId);
    }

    @Override
    public Optional<EnterpriseUserLinkModel> findActiveLink(Long enterpriseId, Long userId) {
        return datasource.findByEnterpriseIdAndUserIdAndStatusIn(enterpriseId, userId, ACTIVE_STATUSES)
            .map(this::toModel);
    }

    @Override
    public CreditPage<EnterpriseUserLinkModel> findByEnterpriseId(Long enterpriseId, int page, int size) {
        Page<EnterpriseUserLinkEntity> result = datasource.findByEnterpriseId(enterpriseId,
            PageRequest.of(page - 1, size, Sort.by("createdAt").descending()));
        return toCreditPage(result, page, size);
    }

    @Override
    public CreditPage<EnterpriseUserLinkModel> findByStatus(EnterpriseLinkStatus status, int page, int size) {
        Page<EnterpriseUserLinkEntity> result = datasource.findByStatus(status,
            PageRequest.of(page - 1, size, Sort.by("createdAt").ascending()));
        return toCreditPage(result, page, size);
    }

    @Override
    public List<EnterpriseUserLinkModel> findByUserId(Long userId) {
        return datasource.findByUserId(userId).stream().map(this::toModel).toList();
    }

    @Override
    public List<EnterpriseUserLinkModel> findApprovedByUserId(Long userId) {
        return datasource.findByUserIdAndStatus(userId, EnterpriseLinkStatus.APPROVED).stream()
            .map(this::toModel)
            .toList();
    }

    @Override
    public List<EnterpriseUserLinkModel> findApprovedByEnterpriseId(Long enterpriseId) {
        return datasource.findByEnterpriseIdAndStatus(enterpriseId, EnterpriseLinkStatus.APPROVED).stream()
            .map(this::toModel)
            .toList();
    }

    @Override
    public EnterpriseUserLinkModel updateStatus(Long id, EnterpriseLinkStatus status, String decidedByUsername,
            Instant decidedAt) {
        datasource.updateStatus(id, status, decidedByUsername, decidedAt);
        return findById(id).orElseThrow();
    }

    @Override
    public EnterpriseUserLinkModel decideIfPending(Long id, EnterpriseLinkStatus status, String decidedByUsername,
            Instant decidedAt) {
        int updated = datasource.decideIfPending(id, status, decidedByUsername, decidedAt);
        if (updated == 0) {
            throw new OptimisticLockingFailureException("EnterpriseUserLink " + id + " is no longer PENDING");
        }
        return findById(id).orElseThrow();
    }

    private EnterpriseUserLinkModel toModel(EnterpriseUserLinkEntity entity) {
        return EnterpriseUserLinkEntityMapper.mapFromEntity(entity,
            datasource.findEnterprisePublicId(entity.getEnterpriseId()).orElse(null),
            datasource.findUserPublicId(entity.getUserId()).orElse(null),
            datasource.findUsername(entity.getUserId()).orElse(null));
    }

    private CreditPage<EnterpriseUserLinkModel> toCreditPage(Page<EnterpriseUserLinkEntity> result, int page,
            int size) {
        return CreditPage.of(result.getContent().stream().map(this::toModel).toList(),
            page, size, result.getTotalElements());
    }
}
