package com.credit.bladi.repository.servicing;

import com.credit.bladi.datasource.servicing.InstallmentDatasource;
import com.credit.bladi.datasource.servicing.entity.InstallmentEntity;
import com.credit.bladi.datasource.servicing.mapper.InstallmentEntityMapper;
import com.credit.bladi.model.enums.InstallmentStatus;
import com.credit.bladi.model.servicing.InstallmentModel;
import com.credit.bladi.model.servicing.ScheduledInstallment;
import com.credit.bladi.model.vo.Money;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class InstallmentRepository implements IInstallmentRepository {
    private static final List<InstallmentStatus> SETTLED_STATUSES = List.of(InstallmentStatus.PAID,
        InstallmentStatus.REPAID_EARLY);

    private static final List<InstallmentStatus> UNPAID_STATUSES = List.of(InstallmentStatus.PENDING,
        InstallmentStatus.LATE);

    private final InstallmentDatasource datasource;

    public InstallmentRepository(InstallmentDatasource datasource) {
        this.datasource = datasource;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public List<InstallmentModel> createSchedule(Long creditFileId, List<ScheduledInstallment> schedule) {
        List<InstallmentEntity> entities = new ArrayList<>();
        int sequenceNumber = 1;
        for (ScheduledInstallment line : schedule) {
            entities.add(InstallmentEntityMapper.mapToNewEntity(creditFileId, sequenceNumber++, line.dueDate(),
                line.amount()));
        }
        datasource.saveAll(entities);
        return entities.stream().map(InstallmentEntityMapper::mapFromEntity).toList();
    }

    @Override
    public Optional<InstallmentModel> findById(Long id) {
        return datasource.findById(id).map(InstallmentEntityMapper::mapFromEntity);
    }

    @Override
    public Optional<Long> findIdByPublicId(UUID publicId) {
        return datasource.findByPublicId(publicId).map(InstallmentEntity::getId);
    }

    @Override
    public List<InstallmentModel> findByCreditFileId(Long creditFileId) {
        return datasource.findByCreditFileIdOrderBySequenceNumberAsc(creditFileId).stream()
            .map(InstallmentEntityMapper::mapFromEntity)
            .toList();
    }

    @Override
    public boolean hasUnsettledInstallments(Long creditFileId) {
        return datasource.existsByCreditFileIdAndStatusNotIn(creditFileId, SETTLED_STATUSES);
    }

    @Override
    public List<InstallmentModel> findOverdueUnpaid(LocalDate today, int page, int size) {
        return datasource.findByStatusInAndDueDateLessThan(UNPAID_STATUSES, today,
                PageRequest.of(page, size, Sort.by("id"))).stream()
            .map(InstallmentEntityMapper::mapFromEntity)
            .toList();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public InstallmentModel markLate(Long id) {
        datasource.markLateIfPending(id);
        return findById(id).orElseThrow();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public InstallmentModel declare(Long id, String declaredBy, String declaredSourceRib, Instant declaredAt) {
        int updated = datasource.declareIfPendingOrLate(id, declaredBy, declaredSourceRib, declaredAt);
        if (updated == 0) {
            throw new OptimisticLockingFailureException("Installment " + id + " is no longer PENDING/LATE");
        }
        return findById(id).orElseThrow();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public InstallmentModel reject(Long id, InstallmentStatus revertedStatus) {
        int updated = datasource.rejectIfDeclared(id, revertedStatus);
        if (updated == 0) {
            throw new OptimisticLockingFailureException("Installment " + id + " is no longer DECLARED");
        }
        return findById(id).orElseThrow();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public InstallmentModel markPaid(Long id, Instant paidAt, Money latePenalty) {
        int updated = datasource.markPaidIfDeclared(id, paidAt, latePenalty.amount());
        if (updated == 0) {
            throw new OptimisticLockingFailureException("Installment " + id + " is no longer DECLARED");
        }
        return findById(id).orElseThrow();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void markRepaidEarly(List<Long> ids, Instant paidAt) {
        int updated = datasource.markRepaidEarlyIfPending(ids, paidAt);
        if (updated != ids.size()) {
            throw new OptimisticLockingFailureException("Some installments of " + ids + " are no longer PENDING");
        }
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void reschedule(Map<Long, Money> newAmountsById) {
        List<InstallmentEntity> entities = datasource.findAllById(newAmountsById.keySet());
        entities.forEach(entity -> entity.setAmount(newAmountsById.get(entity.getId()).amount()));
        datasource.saveAllAndFlush(entities);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public InstallmentModel recordReminderSent(Long id, Instant sentAt) {
        datasource.recordReminderSentAt(id, sentAt);
        return findById(id).orElseThrow();
    }
}
