package com.credit.bladi.datasource.servicing.mapper;

import com.credit.bladi.datasource.servicing.entity.PaymentEntity;
import com.credit.bladi.model.servicing.PaymentModel;
import com.credit.bladi.model.vo.Money;
import java.time.Instant;

public final class PaymentEntityMapper {
    private PaymentEntityMapper() {
    }

    public static PaymentModel mapFromEntity(PaymentEntity entity) {
        return new PaymentModel(
            entity.getId(),
            entity.getInstallmentId(),
            new Money(entity.getAmount()),
            entity.getPaidAt());
    }

    public static PaymentEntity mapToNewEntity(Long installmentId, Money amount, Instant paidAt) {
        PaymentEntity entity = new PaymentEntity();
        entity.setInstallmentId(installmentId);
        entity.setAmount(amount.amount());
        entity.setPaidAt(paidAt);
        return entity;
    }
}
