package com.credit.bladi.service.bamparameters;

import static com.credit.bladi.exception.BamParametersException.BamParametersExceptionType.NOT_FOUND;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.exception.BamParametersException;
import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.bam.BamParametersPatchResult;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.repository.bam.IBamParametersRepository;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.security.ICurrentActorProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BamParametersService implements IBamParametersService {

    private static final Logger LOGGER = LogManager.getLogger(BamParametersService.class);

    private final IBamParametersRepository repository;

    private final IAuditService auditService;

    private final ICurrentActorProvider currentActorProvider;

    public BamParametersService(IBamParametersRepository repository, IAuditService auditService,
                                ICurrentActorProvider currentActorProvider) {
        this.repository = repository;
        this.auditService = auditService;
        this.currentActorProvider = currentActorProvider;
    }

    @Override
    @Transactional(readOnly = true)
    public CreditPage<BamParametersModel> getAllParameters(int page, int size) {
        LOGGER.info("GetAllParameters - page={}, size={}", page, size);
        return repository.findAll(page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public BamParametersModel getById(Long id) {
        LOGGER.info("GetById - id={}", id);
        return repository.findById(id)
            .orElseThrow(() -> BamParametersException.of(NOT_FOUND));
    }

    @Override
    public BamParametersModel patchParameters(BamParametersModel bamParametersPatch) {
        LOGGER.info("PatchParameters - id={}", bamParametersPatch.getId());
        BamParametersPatchResult result = repository.patchParameters(bamParametersPatch);
        auditService.log(currentActorProvider.getCurrentUsername(), AuditAction.BAM_PARAMETERS_UPDATE,
            "id=" + bamParametersPatch.getId() + ", changes=["
                + result.after().describeChangesFrom(result.before()) + "]");
        return result.after();
    }
}