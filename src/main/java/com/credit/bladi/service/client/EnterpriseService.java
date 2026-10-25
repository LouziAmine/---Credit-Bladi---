package com.credit.bladi.service.client;

import static com.credit.bladi.exception.EnterpriseException.EnterpriseExceptionType.ENTERPRISE_NOT_FOUND;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.exception.EnterpriseException;
import com.credit.bladi.model.audit.AuditTarget;
import com.credit.bladi.model.client.EnterpriseModel;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.enums.AuditEntityType;
import com.credit.bladi.model.vo.ValueObjectMappers;
import com.credit.bladi.repository.client.IEnterpriseRepository;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.security.ICurrentActorProvider;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EnterpriseService implements IEnterpriseService {
    private final IEnterpriseRepository repository;

    private final IAuditService auditService;

    private final ICurrentActorProvider currentActorProvider;

    public EnterpriseService(IEnterpriseRepository repository, IAuditService auditService,
                             ICurrentActorProvider currentActorProvider) {
        this.repository = repository;
        this.auditService = auditService;
        this.currentActorProvider = currentActorProvider;
    }

    @Override
    @Transactional
    public EnterpriseModel createEnterprise(String name, String sector, String registrationNumber) {
        EnterpriseModel created = repository.create(name, sector, registrationNumber);
        auditService.log(currentActorProvider.getCurrentUsername(), AuditAction.ENTERPRISE_CREATE,
            new AuditTarget(AuditEntityType.ENTERPRISE, created.publicId()),
            "enterpriseId=" + created.id() + ", name=" + created.name());
        return created;
    }

    @Override
    @Transactional(readOnly = true)
    public CreditPage<EnterpriseModel> getAllEnterprises(int page, int size) {
        return repository.findAll(page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public EnterpriseModel getEnterpriseById(Long id) {
        return repository.findById(id).orElseThrow(() -> EnterpriseException.of(ENTERPRISE_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnterpriseModel> getEnterprisesByIds(List<Long> ids) {
        return repository.findAllByIds(ids);
    }

    @Override
    @Transactional(readOnly = true)
    public String getRibOrNull(Long enterpriseId) {
        return enterpriseId != null ? ValueObjectMappers.toRibValue(getEnterpriseById(enterpriseId).rib()) : null;
    }

    @Override
    @Transactional(readOnly = true)
    public Long resolveEnterpriseId(UUID publicId) {
        return repository.findIdByPublicId(publicId)
            .orElseThrow(() -> EnterpriseException.of(ENTERPRISE_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EnterpriseModel> matchEnterpriseByName(String rawName) {
        return repository.findMatchByName(rawName);
    }
}
