package com.credit.bladi.service.creditrate;

import static com.credit.bladi.exception.CreditRateException.CreditRateExceptionType.NOT_FOUND;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.exception.CreditRateException;
import com.credit.bladi.model.creditrate.CreditRateModel;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.parameters.RegulatoryLimits;
import com.credit.bladi.model.parameters.VersionedPatchResult;
import com.credit.bladi.repository.creditrate.ICreditRateRepository;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.parameters.AbstractVersionedParametersService;
import com.credit.bladi.service.security.ICurrentActorProvider;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreditRateService extends AbstractVersionedParametersService<CreditRateModel>
        implements ICreditRateService {
    private final ICreditRateRepository repository;

    public CreditRateService(ICreditRateRepository repository, IAuditService auditService,
                              ICurrentActorProvider currentActorProvider,
                              RegulatoryLimits regulatoryLimits) {
        super(auditService, currentActorProvider, AuditAction.CREDIT_RATE_UPDATE, regulatoryLimits);
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public CreditPage<CreditRateModel> getAllParameters(int page, int size) {
        return repository.findAll(page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public Long resolveCreditRateId(UUID publicId) {
        return resolveId(publicId);
    }

    @Override
    protected Optional<CreditRateModel> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    protected Optional<Long> findIdByPublicId(UUID publicId) {
        return repository.findIdByPublicId(publicId);
    }

    @Override
    protected RuntimeException notFoundException() {
        return CreditRateException.of(NOT_FOUND);
    }

    @Override
    protected VersionedPatchResult<CreditRateModel> patchInRepository(CreditRateModel patch) {
        return repository.patchParameters(patch);
    }
}
