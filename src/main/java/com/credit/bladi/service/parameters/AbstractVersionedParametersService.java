package com.credit.bladi.service.parameters;

import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.parameters.RegulatoryLimits;
import com.credit.bladi.model.parameters.VersionedParametersModel;
import com.credit.bladi.model.parameters.VersionedPatchResult;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.security.ICurrentActorProvider;
import java.util.Optional;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractVersionedParametersService<M extends VersionedParametersModel<M>> {
    private final Logger logger = LogManager.getLogger(getClass());

    private final IAuditService auditService;

    private final ICurrentActorProvider currentActorProvider;

    private final AuditAction patchAuditAction;

    private final RegulatoryLimits regulatoryLimits;

    protected AbstractVersionedParametersService(IAuditService auditService,
            ICurrentActorProvider currentActorProvider, AuditAction patchAuditAction,
            RegulatoryLimits regulatoryLimits) {
        this.auditService = auditService;
        this.currentActorProvider = currentActorProvider;
        this.patchAuditAction = patchAuditAction;
        this.regulatoryLimits = regulatoryLimits;
    }

    protected abstract Optional<M> findById(Long id);

    protected abstract Optional<Long> findIdByPublicId(UUID publicId);

    protected abstract RuntimeException notFoundException();

    protected abstract VersionedPatchResult<M> patchInRepository(M patch);

    @Transactional(readOnly = true)
    public M getById(Long id) {
        logger.info("GetById - id={}", id);
        return findById(id).orElseThrow(this::notFoundException);
    }

    @Transactional(readOnly = true)
    public Long resolveId(UUID publicId) {
        return findIdByPublicId(publicId).orElseThrow(this::notFoundException);
    }

    @Transactional
    public M patchParameters(M patch) {
        logger.info("PatchParameters - id={}", patch.getId());
        VersionedPatchResult<M> result = patchInRepository(patch);
        result.after().highestAnnualRate().ifPresent(regulatoryLimits::assertWithinMaxRate);
        String changes = result.after().describeChangesFrom(result.before());
        if (!changes.isEmpty()) {
            auditService.log(currentActorProvider.getCurrentUsername(), patchAuditAction,
                "id=" + patch.getId() + ", changes=[" + changes + "]");
        }
        return result.after();
    }
}
