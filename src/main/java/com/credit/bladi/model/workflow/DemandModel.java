package com.credit.bladi.model.workflow;

import static com.credit.bladi.exception.WorkflowException.WorkflowExceptionType.NO_NEXT_STEP;

import com.credit.bladi.exception.WorkflowException;
import com.credit.bladi.model.enums.DemandStatus;
import com.credit.bladi.model.enums.DemandType;
import com.credit.bladi.model.enums.Language;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import java.time.Instant;
import java.util.UUID;

public record DemandModel(
        Long id,
        UUID publicId,
        String reference,
        Long workflowId,
        DemandType demandType,
        String contactName,
        Money amount,
        Integer duration,
        Instant createdAt,
        Instant completedAt,
        Rate applicableRate,
        String contactEmail,
        String contactPhone,
        Language contactLanguage,
        Instant rejectedAt,
        DemandDetail detail
) {
    public boolean isCompleted() {
        return completedAt != null;
    }

    public boolean isRejected() {
        return rejectedAt != null;
    }

    public DemandStatus status() {
        if (isRejected()) {
            return DemandStatus.REJECTED;
        }
        return isCompleted() ? DemandStatus.COMPLETED : DemandStatus.IN_PROGRESS;
    }

    public void assertOpen() {
        if (status() != DemandStatus.IN_PROGRESS) {
            throw WorkflowException.of(NO_NEXT_STEP);
        }
    }

    public EnterpriseDemandDetail enterpriseDetail() {
        if (detail instanceof EnterpriseDemandDetail enterprise) {
            return enterprise;
        }
        throw new IllegalStateException("Demand " + reference + " is a " + demandType + " demand, not ENTERPRISE");
    }

    public MortgageDemandDetail mortgageDetail() {
        if (detail instanceof MortgageDemandDetail mortgage) {
            return mortgage;
        }
        throw new IllegalStateException("Demand " + reference + " is a " + demandType + " demand, not MORTGAGE");
    }

    public boolean isMourabaha() {
        return detail instanceof MortgageDemandDetail mortgage && mortgage.productType() == ProductType.MOURABAHA;
    }

    public Long enterpriseIdOrNull() {
        return detail instanceof EnterpriseDemandDetail enterprise ? enterprise.enterpriseId() : null;
    }
}
