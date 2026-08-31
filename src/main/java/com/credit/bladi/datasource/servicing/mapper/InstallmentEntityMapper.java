package com.credit.bladi.datasource.servicing.mapper;

import com.credit.bladi.datasource.servicing.entity.InstallmentEntity;
import com.credit.bladi.model.enums.InstallmentStatus;
import com.credit.bladi.model.servicing.InstallmentModel;
import com.credit.bladi.model.vo.Money;
import java.time.LocalDate;

public final class InstallmentEntityMapper {
    private InstallmentEntityMapper() {
    }

    public static InstallmentModel mapFromEntity(InstallmentEntity entity) {
        return new InstallmentModel(
            entity.getId(),
            entity.getPublicId(),
            entity.getCreditFileId(),
            entity.getSequenceNumber(),
            entity.getDueDate(),
            new Money(entity.getAmount()),
            entity.getStatus(),
            entity.getPaidAt(),
            entity.getLastReminderSentAt(),
            entity.getDeclaredAt(),
            entity.getDeclaredBy(),
            entity.getDeclaredSourceRib(),
            entity.getLatePenalty() != null ? new Money(entity.getLatePenalty()) : null);
    }

    public static InstallmentEntity mapToNewEntity(Long creditFileId, int sequenceNumber, LocalDate dueDate,
            Money amount) {
        InstallmentEntity entity = new InstallmentEntity();
        entity.setCreditFileId(creditFileId);
        entity.setSequenceNumber(sequenceNumber);
        entity.setDueDate(dueDate);
        entity.setAmount(amount.amount());
        entity.setStatus(InstallmentStatus.PENDING);
        return entity;
    }
}
