package com.credit.bladi.repository.servicing;

import com.credit.bladi.model.enums.InstallmentStatus;
import com.credit.bladi.model.servicing.InstallmentModel;
import com.credit.bladi.model.servicing.ScheduledInstallment;
import com.credit.bladi.model.vo.Money;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface IInstallmentRepository {
    List<InstallmentModel> createSchedule(Long creditFileId, List<ScheduledInstallment> schedule);

    Optional<InstallmentModel> findById(Long id);

    Optional<Long> findIdByPublicId(UUID publicId);

    List<InstallmentModel> findByCreditFileId(Long creditFileId);

    boolean hasUnsettledInstallments(Long creditFileId);

    List<InstallmentModel> findOverdueUnpaid(LocalDate today, int page, int size);

    InstallmentModel markLate(Long id);

    InstallmentModel declare(Long id, String declaredBy, String declaredSourceRib, Instant declaredAt);

    InstallmentModel reject(Long id, InstallmentStatus revertedStatus);

    InstallmentModel markPaid(Long id, Instant paidAt, Money latePenalty);

    void markRepaidEarly(List<Long> ids, Instant paidAt);

    void reschedule(Map<Long, Money> newAmountsById);

    InstallmentModel recordReminderSent(Long id, Instant sentAt);
}
