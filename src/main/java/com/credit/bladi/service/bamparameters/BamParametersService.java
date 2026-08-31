package com.credit.bladi.service.bamparameters;

import static com.credit.bladi.exception.BamParametersException.BamParametersExceptionType.NOT_FOUND;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.exception.BamParametersException;
import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.parameters.RegulatoryLimits;
import com.credit.bladi.model.parameters.VersionedPatchResult;
import com.credit.bladi.repository.bam.IBamParametersRepository;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.parameters.AbstractVersionedParametersService;
import com.credit.bladi.service.security.ICurrentActorProvider;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BamParametersService extends AbstractVersionedParametersService<BamParametersModel>
        implements IBamParametersService {
    private final IBamParametersRepository repository;

    public BamParametersService(IBamParametersRepository repository, IAuditService auditService,
                                ICurrentActorProvider currentActorProvider,
                                RegulatoryLimits regulatoryLimits) {
        super(auditService, currentActorProvider, AuditAction.BAM_PARAMETERS_UPDATE, regulatoryLimits);
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public CreditPage<BamParametersModel> getAllParameters(int page, int size) {
        return repository.findAll(page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public Long resolveBamParametersId(UUID publicId) {
        return resolveId(publicId);
    }

    @Override
    protected Optional<BamParametersModel> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    protected Optional<Long> findIdByPublicId(UUID publicId) {
        return repository.findIdByPublicId(publicId);
    }

    @Override
    protected RuntimeException notFoundException() {
        return BamParametersException.of(NOT_FOUND);
    }

    @Override
    protected VersionedPatchResult<BamParametersModel> patchInRepository(BamParametersModel patch) {
        return repository.patchParameters(patch);
    }
}
