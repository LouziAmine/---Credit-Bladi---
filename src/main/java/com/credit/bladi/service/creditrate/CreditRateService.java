package com.credit.bladi.service.creditrate;

import static com.credit.bladi.exception.CreditRateException.CreditRateExceptionType.NOT_FOUND;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.exception.CreditRateException;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.simulation.CreditRateModel;
import com.credit.bladi.model.simulation.CreditRatePatchResult;
import com.credit.bladi.repository.creditrate.ICreditRateRepository;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.security.ICurrentActorProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreditRateService implements ICreditRateService {

    private static final Logger LOGGER = LogManager.getLogger(CreditRateService.class);

    private final ICreditRateRepository repository;

    private final IAuditService auditService;

    private final ICurrentActorProvider currentActorProvider;

    public CreditRateService(ICreditRateRepository repository, IAuditService auditService,
                              ICurrentActorProvider currentActorProvider) {
        this.repository = repository;
        this.auditService = auditService;
        this.currentActorProvider = currentActorProvider;
    }

    @Override
    @Transactional(readOnly = true)
    public CreditPage<CreditRateModel> getAllParameters(int page, int size) {
        LOGGER.info("GetAllParameters - page={}, size={}", page, size);
        return repository.findAll(page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public CreditRateModel getById(Long id) {
        LOGGER.info("GetById - id={}", id);
        return repository.findById(id)
            .orElseThrow(() -> CreditRateException.of(NOT_FOUND));
    }

    @Override
    public CreditRateModel patchParameters(CreditRateModel creditRatePatch) {
        LOGGER.info("PatchParameters - id={}", creditRatePatch.getId());
        CreditRatePatchResult result = repository.patchParameters(creditRatePatch);
        auditService.log(currentActorProvider.getCurrentUsername(), AuditAction.CREDIT_RATE_UPDATE,
            "id=" + creditRatePatch.getId() + ", changes=["
                + result.after().describeChangesFrom(result.before()) + "]");
        return result.after();
    }
}
