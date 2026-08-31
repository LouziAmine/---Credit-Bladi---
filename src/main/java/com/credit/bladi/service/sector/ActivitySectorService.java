package com.credit.bladi.service.sector;

import static com.credit.bladi.exception.ActivitySectorException.ActivitySectorExceptionType.DUPLICATE_NAME;

import com.credit.bladi.exception.ActivitySectorException;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.sector.ActivitySectorModel;
import com.credit.bladi.repository.sector.IActivitySectorRepository;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.security.ICurrentActorProvider;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActivitySectorService implements IActivitySectorService {
    private final IActivitySectorRepository repository;

    private final IAuditService auditService;

    private final ICurrentActorProvider currentActorProvider;

    public ActivitySectorService(IActivitySectorRepository repository, IAuditService auditService,
                                  ICurrentActorProvider currentActorProvider) {
        this.repository = repository;
        this.auditService = auditService;
        this.currentActorProvider = currentActorProvider;
    }

    @Override
    @Transactional
    public ActivitySectorModel createActivitySector(String name) {
        if (repository.existsByNameIgnoreCase(name)) {
            throw ActivitySectorException.of(DUPLICATE_NAME);
        }
        ActivitySectorModel created = repository.create(name);
        auditService.log(currentActorProvider.getCurrentUsername(), AuditAction.ACTIVITY_SECTOR_CREATE,
            "activitySectorId=" + created.id() + ", name=" + created.name());
        return created;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivitySectorModel> getAllActivitySectors() {
        return repository.findAll();
    }
}
