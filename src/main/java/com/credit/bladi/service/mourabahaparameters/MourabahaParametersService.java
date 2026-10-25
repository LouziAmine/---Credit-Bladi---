package com.credit.bladi.service.mourabahaparameters;

import static com.credit.bladi.exception.MourabahaParametersException.MourabahaParametersExceptionType.NOT_FOUND;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.exception.MourabahaParametersException;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.mourabaha.MourabahaParametersModel;
import com.credit.bladi.model.parameters.RegulatoryLimits;
import com.credit.bladi.model.parameters.VersionedPatchResult;
import com.credit.bladi.repository.mourabaha.IMourabahaParametersRepository;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.parameters.AbstractVersionedParametersService;
import com.credit.bladi.service.security.ICurrentActorProvider;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MourabahaParametersService extends AbstractVersionedParametersService<MourabahaParametersModel>
        implements IMourabahaParametersService {
    private final IMourabahaParametersRepository repository;

    public MourabahaParametersService(IMourabahaParametersRepository repository, IAuditService auditService,
                                      ICurrentActorProvider currentActorProvider,
                                      RegulatoryLimits regulatoryLimits) {
        super(auditService, currentActorProvider, AuditAction.MOURABAHA_PARAMETERS_UPDATE, regulatoryLimits);
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public CreditPage<MourabahaParametersModel> getAllParameters(int page, int size) {
        return repository.findAll(page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public Long resolveMourabahaParametersId(UUID publicId) {
        return resolveId(publicId);
    }

    @Override
    protected Optional<MourabahaParametersModel> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    protected Optional<Long> findIdByPublicId(UUID publicId) {
        return repository.findIdByPublicId(publicId);
    }

    @Override
    protected RuntimeException notFoundException() {
        return MourabahaParametersException.of(NOT_FOUND);
    }

    @Override
    protected VersionedPatchResult<MourabahaParametersModel> patchInRepository(MourabahaParametersModel patch) {
        return repository.patchParameters(patch);
    }
}
