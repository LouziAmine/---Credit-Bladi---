package com.credit.bladi.service.mourabahaparameters;

import static com.credit.bladi.exception.MourabahaParametersException.MourabahaParametersExceptionType.NOT_FOUND;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.exception.MourabahaParametersException;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.mourabaha.MourabahaParametersModel;
import com.credit.bladi.model.mourabaha.MourabahaParametersPatchResult;
import com.credit.bladi.repository.mourabaha.IMourabahaParametersRepository;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.security.ICurrentActorProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MourabahaParametersService implements IMourabahaParametersService {

    private static final Logger LOGGER = LogManager.getLogger(MourabahaParametersService.class);

    private final IMourabahaParametersRepository repository;

    private final IAuditService auditService;

    private final ICurrentActorProvider currentActorProvider;

    public MourabahaParametersService(IMourabahaParametersRepository repository, IAuditService auditService,
                                      ICurrentActorProvider currentActorProvider) {
        this.repository = repository;
        this.auditService = auditService;
        this.currentActorProvider = currentActorProvider;
    }

    @Override
    @Transactional(readOnly = true)
    public CreditPage<MourabahaParametersModel> getAllParameters(int page, int size) {
        LOGGER.info("GetAllParameters - page={}, size={}", page, size);
        return repository.findAll(page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public MourabahaParametersModel getById(Long id) {
        LOGGER.info("GetById - id={}", id);
        return repository.findById(id)
            .orElseThrow(() -> MourabahaParametersException.of(NOT_FOUND));
    }

    @Override
    public MourabahaParametersModel patchParameters(MourabahaParametersModel mourabahaParametersPatch) {
        LOGGER.info("PatchParameters - id={}", mourabahaParametersPatch.getId());
        MourabahaParametersPatchResult result = repository.patchParameters(mourabahaParametersPatch);
        auditService.log(currentActorProvider.getCurrentUsername(), AuditAction.MOURABAHA_PARAMETERS_UPDATE,
            "id=" + mourabahaParametersPatch.getId() + ", changes=["
                + result.after().describeChangesFrom(result.before()) + "]");
        return result.after();
    }
}
