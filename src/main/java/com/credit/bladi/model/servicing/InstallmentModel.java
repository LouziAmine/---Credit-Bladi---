package com.credit.bladi.model.servicing;

import static com.credit.bladi.exception.CreditFileException.CreditFileExceptionType.INSTALLMENT_ALREADY_DECLARED;
import static com.credit.bladi.exception.CreditFileException.CreditFileExceptionType.INSTALLMENT_ALREADY_PAID;
import static com.credit.bladi.exception.CreditFileException.CreditFileExceptionType.INSTALLMENT_NOT_DECLARED;
import static com.credit.bladi.exception.CreditFileException.CreditFileExceptionType.INSTALLMENT_NOT_FOR_CREDIT_FILE;

import com.credit.bladi.exception.CreditFileException;
import com.credit.bladi.model.enums.InstallmentStatus;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rib;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

public record InstallmentModel(
        Long id,
        UUID publicId,
        Long creditFileId,
        int sequenceNumber,
        LocalDate dueDate,
        Money amount,
        InstallmentStatus status,
        Instant paidAt,
        Instant lastReminderSentAt,

        Instant declaredAt,

        String declaredBy,

        String declaredSourceRib,

        Money latePenalty
) {
    public boolean isSettled() {
        return status == InstallmentStatus.PAID || status == InstallmentStatus.REPAID_EARLY;
    }

    public boolean isInArrears() {
        return status == InstallmentStatus.LATE || status == InstallmentStatus.DECLARED;
    }

    public LocalDate lateInterestEndDate(LocalDate today, ZoneId zone) {
        return declaredAt != null && isDeclared() ? LocalDate.ofInstant(declaredAt, zone) : today;
    }

    public InstallmentModel withLatePenalty(Money penalty) {
        return new InstallmentModel(id, publicId, creditFileId, sequenceNumber, dueDate, amount, status, paidAt,
            lastReminderSentAt, declaredAt, declaredBy, declaredSourceRib, penalty);
    }

    public Money totalDue() {
        return latePenalty == null ? amount : amount.add(latePenalty);
    }

    public boolean isDeclared() {
        return status == InstallmentStatus.DECLARED;
    }

    public boolean wasRemindedSince(Instant cutoff) {
        return lastReminderSentAt != null && lastReminderSentAt.isAfter(cutoff);
    }

    public void assertBelongsTo(CreditFileModel creditFile) {
        if (!creditFileId.equals(creditFile.id())) {
            throw CreditFileException.of(INSTALLMENT_NOT_FOR_CREDIT_FILE);
        }
    }

    public InstallmentModel declare(String by, Rib sourceRib, Instant at) {
        if (isSettled()) {
            throw CreditFileException.of(INSTALLMENT_ALREADY_PAID);
        }
        if (isDeclared()) {
            throw CreditFileException.of(INSTALLMENT_ALREADY_DECLARED);
        }
        return new InstallmentModel(id, publicId, creditFileId, sequenceNumber, dueDate, amount,
            InstallmentStatus.DECLARED, paidAt, lastReminderSentAt, at, by, sourceRib.value(), latePenalty);
    }

    public InstallmentModel validate(Instant at, Money lateInterest) {
        assertDeclared();
        return new InstallmentModel(id, publicId, creditFileId, sequenceNumber, dueDate, amount,
            InstallmentStatus.PAID, at, lastReminderSentAt, declaredAt, declaredBy, declaredSourceRib, lateInterest);
    }

    public InstallmentModel reject(LocalDate today) {
        assertDeclared();
        InstallmentStatus reverted = dueDate.isBefore(today) ? InstallmentStatus.LATE : InstallmentStatus.PENDING;
        return new InstallmentModel(id, publicId, creditFileId, sequenceNumber, dueDate, amount, reverted,
            paidAt, lastReminderSentAt, declaredAt, declaredBy, declaredSourceRib, latePenalty);
    }

    private void assertDeclared() {
        if (!isDeclared()) {
            throw CreditFileException.of(INSTALLMENT_NOT_DECLARED);
        }
    }
}
