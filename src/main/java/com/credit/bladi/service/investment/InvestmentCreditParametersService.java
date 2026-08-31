package com.credit.bladi.service.investment;

import static com.credit.bladi.exception.InvestmentCreditException.InvestmentCreditExceptionType.PARAMETERS_NOT_FOUND;

import com.credit.bladi.exception.InvestmentCreditException;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.model.investment.InvestmentCreditParametersModel;
import com.credit.bladi.model.parameters.RegulatoryLimits;
import com.credit.bladi.model.parameters.VersionedPatchResult;
import com.credit.bladi.repository.investment.IInvestmentCreditParametersRepository;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.parameters.AbstractVersionedParametersService;
import com.credit.bladi.service.security.ICurrentActorProvider;
import java.util.Optional;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvestmentCreditParametersService
        extends AbstractVersionedParametersService<InvestmentCreditParametersModel>
        implements IInvestmentCreditParametersService {
    private static final Logger LOGGER = LogManager.getLogger(InvestmentCreditParametersService.class);

    private final IInvestmentCreditParametersRepository repository;

    public InvestmentCreditParametersService(IInvestmentCreditParametersRepository repository,
                                             IAuditService auditService,
                                             ICurrentActorProvider currentActorProvider,
                                             RegulatoryLimits regulatoryLimits) {
        super(auditService, currentActorProvider, AuditAction.ENTERPRISE_CREDIT_PARAMETERS_UPDATE,
            regulatoryLimits);
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public Long resolveParametersId(UUID publicId) {
        return resolveId(publicId);
    }

    @Override
    @Transactional(readOnly = true)
    public InvestmentCreditParametersModel getActiveByEnterpriseType(EnterpriseType enterpriseType) {
        LOGGER.info("GetActiveByEnterpriseType - enterpriseType={}", enterpriseType);
        return repository.findActiveByEnterpriseType(enterpriseType)
            .orElseThrow(() -> InvestmentCreditException.of(PARAMETERS_NOT_FOUND));
    }

    @Override
    protected Optional<InvestmentCreditParametersModel> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    protected Optional<Long> findIdByPublicId(UUID publicId) {
        return repository.findIdByPublicId(publicId);
    }

    @Override
    protected RuntimeException notFoundException() {
        return InvestmentCreditException.of(PARAMETERS_NOT_FOUND);
    }

    @Override
    protected VersionedPatchResult<InvestmentCreditParametersModel> patchInRepository(
            InvestmentCreditParametersModel patch) {
        return repository.patchParameters(patch);
    }
}
