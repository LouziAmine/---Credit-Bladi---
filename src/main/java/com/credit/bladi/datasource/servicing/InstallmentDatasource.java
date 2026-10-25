package com.credit.bladi.datasource.servicing;

import com.credit.bladi.datasource.servicing.entity.InstallmentEntity;
import com.credit.bladi.model.enums.InstallmentStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InstallmentDatasource extends JpaRepository<InstallmentEntity, Long> {
    Optional<InstallmentEntity> findByPublicId(UUID publicId);

    List<InstallmentEntity> findByCreditFileIdOrderBySequenceNumberAsc(Long creditFileId);

    boolean existsByCreditFileIdAndStatusNotIn(Long creditFileId, List<InstallmentStatus> statuses);

    List<InstallmentEntity> findByStatusInAndDueDateLessThan(
        List<InstallmentStatus> statuses, LocalDate date, Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE InstallmentEntity e SET e.status = com.credit.bladi.model.enums.InstallmentStatus.DECLARED, "
        + "e.declaredBy = :declaredBy, e.declaredSourceRib = :declaredSourceRib, e.declaredAt = :declaredAt "
        + "WHERE e.id = :id AND e.status IN (com.credit.bladi.model.enums.InstallmentStatus.PENDING, "
        + "com.credit.bladi.model.enums.InstallmentStatus.LATE)")
    int declareIfPendingOrLate(@Param("id") Long id, @Param("declaredBy") String declaredBy,
        @Param("declaredSourceRib") String declaredSourceRib, @Param("declaredAt") Instant declaredAt);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE InstallmentEntity e SET e.status = com.credit.bladi.model.enums.InstallmentStatus.PAID, "
        + "e.paidAt = :paidAt, e.latePenalty = :latePenalty "
        + "WHERE e.id = :id AND e.status = com.credit.bladi.model.enums.InstallmentStatus.DECLARED")
    int markPaidIfDeclared(@Param("id") Long id, @Param("paidAt") Instant paidAt,
        @Param("latePenalty") BigDecimal latePenalty);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE InstallmentEntity e "
        + "SET e.status = com.credit.bladi.model.enums.InstallmentStatus.REPAID_EARLY, e.paidAt = :paidAt "
        + "WHERE e.id IN :ids AND e.status = com.credit.bladi.model.enums.InstallmentStatus.PENDING")
    int markRepaidEarlyIfPending(@Param("ids") List<Long> ids, @Param("paidAt") Instant paidAt);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE InstallmentEntity e SET e.status = :revertedStatus "
        + "WHERE e.id = :id AND e.status = com.credit.bladi.model.enums.InstallmentStatus.DECLARED")
    int rejectIfDeclared(@Param("id") Long id, @Param("revertedStatus") InstallmentStatus revertedStatus);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE InstallmentEntity e SET e.status = com.credit.bladi.model.enums.InstallmentStatus.LATE "
        + "WHERE e.id = :id AND e.status = com.credit.bladi.model.enums.InstallmentStatus.PENDING")
    int markLateIfPending(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE InstallmentEntity e SET e.lastReminderSentAt = :sentAt WHERE e.id = :id")
    int recordReminderSentAt(@Param("id") Long id, @Param("sentAt") Instant sentAt);
}
