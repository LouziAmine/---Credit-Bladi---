package com.credit.bladi.model.workflow;

import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.model.vo.Money;

public record EnterpriseDemandSnapshotDetail(
        EnterpriseType enterpriseType,
        Money annualRevenue,
        Money existingAnnualDebtService,
        Money netAnnualResult,
        Money guaranteesValue,
        String sector,
        Integer companyAgeYears
) implements DemandSnapshotDetail {
}
