package com.credit.bladi.datasource.servicing.mapper;

import com.credit.bladi.datasource.servicing.entity.EarlyRepaymentEntity;
import com.credit.bladi.model.servicing.EarlyRepaymentModel;
import com.credit.bladi.model.servicing.EarlyRepaymentQuote;
import com.credit.bladi.model.vo.Money;
import java.time.Instant;

public final class EarlyRepaymentEntityMapper {
    private EarlyRepaymentEntityMapper() {
    }

    public static EarlyRepaymentEntity mapToNewEntity(Long creditFileId, EarlyRepaymentQuote quote,
            String performedBy, Instant performedAt) {
        EarlyRepaymentEntity entity = new EarlyRepaymentEntity();
        entity.setCreditFileId(creditFileId);
        entity.setFullRepayment(quote.fullRepayment());
        entity.setOutstandingBefore(quote.outstandingBefore().amount());
        entity.setRepaidPrincipal(quote.repaidPrincipal().amount());
        entity.setAccruedInterest(quote.accruedInterest().amount());
        entity.setIndemnity(quote.indemnity().amount());
        entity.setTotalPaid(quote.totalToPay().amount());
        entity.setOutstandingAfter(quote.outstandingAfter().amount());
        entity.setPerformedBy(performedBy);
        entity.setPerformedAt(performedAt);
        return entity;
    }

    public static EarlyRepaymentModel mapFromEntity(EarlyRepaymentEntity entity) {
        return new EarlyRepaymentModel(entity.getId(), entity.getPublicId(), entity.getCreditFileId(),
            Boolean.TRUE.equals(entity.getFullRepayment()), new Money(entity.getOutstandingBefore()),
            new Money(entity.getRepaidPrincipal()), new Money(entity.getAccruedInterest()),
            new Money(entity.getIndemnity()), new Money(entity.getTotalPaid()),
            new Money(entity.getOutstandingAfter()), entity.getPerformedBy(), entity.getPerformedAt());
    }
}
