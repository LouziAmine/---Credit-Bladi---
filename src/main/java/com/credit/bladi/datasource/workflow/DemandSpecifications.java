package com.credit.bladi.datasource.workflow;

import com.credit.bladi.datasource.workflow.entity.DemandEntity;
import com.credit.bladi.datasource.workflow.entity.EnterpriseDemandDetailEntity;
import com.credit.bladi.model.enums.DemandStatus;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.math.BigDecimal;
import org.springframework.data.jpa.domain.Specification;

public final class DemandSpecifications {
    private DemandSpecifications() {
    }

    public static Specification<DemandEntity> contactNameContains(String contactName) {
        String pattern = "%" + contactName.toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("contactName")), pattern);
    }

    public static Specification<DemandEntity> statusIs(DemandStatus status) {
        return switch (status) {
            case REJECTED -> (root, query, cb) -> cb.isNotNull(root.get("rejectedAt"));
            case COMPLETED -> (root, query, cb) -> cb.and(
                cb.isNotNull(root.get("completedAt")), cb.isNull(root.get("rejectedAt")));
            case IN_PROGRESS -> (root, query, cb) -> cb.and(
                cb.isNull(root.get("completedAt")), cb.isNull(root.get("rejectedAt")));
        };
    }

    public static Specification<DemandEntity> amountGreaterThanOrEqualTo(BigDecimal minAmount) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("amount"), minAmount);
    }

    public static Specification<DemandEntity> amountLessThanOrEqualTo(BigDecimal maxAmount) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("amount"), maxAmount);
    }

    public static Specification<DemandEntity> enterpriseIdIs(Long enterpriseId) {
        return (root, query, cb) -> {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<EnterpriseDemandDetailEntity> detail = subquery.from(EnterpriseDemandDetailEntity.class);
            subquery.select(detail.get("demandId")).where(cb.equal(detail.get("enterpriseId"), enterpriseId));
            return root.get("id").in(subquery);
        };
    }
}
