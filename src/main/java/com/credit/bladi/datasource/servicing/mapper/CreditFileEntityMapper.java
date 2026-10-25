package com.credit.bladi.datasource.servicing.mapper;

import com.credit.bladi.datasource.servicing.entity.CreditFileEntity;
import com.credit.bladi.model.enums.CreditFileStatus;
import com.credit.bladi.model.servicing.CreditFileModel;
import com.credit.bladi.model.vo.Money;
import java.time.Instant;

public final class CreditFileEntityMapper {
    private CreditFileEntityMapper() {
    }

    public static CreditFileModel mapFromEntity(CreditFileEntity entity) {
        return new CreditFileModel(
            entity.getId(),
            entity.getPublicId(),
            entity.getDemandId(),
            entity.getReference(),
            entity.getDisbursedAt(),
            new Money(entity.getDisbursedAmount()),
            entity.getStatus(),
            entity.getManagedByUsername());
    }

    public static CreditFileEntity mapToNewEntity(Long demandId, String reference, Instant disbursedAt,
            Money disbursedAmount, String managedByUsername) {
        CreditFileEntity entity = new CreditFileEntity();
        entity.setDemandId(demandId);
        entity.setReference(reference);
        entity.setDisbursedAt(disbursedAt);
        entity.setDisbursedAmount(disbursedAmount.amount());
        entity.setStatus(CreditFileStatus.ACTIVE);
        entity.setManagedByUsername(managedByUsername);
        return entity;
    }
}
