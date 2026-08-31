package com.credit.bladi.model.workflow;

import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.model.vo.Money;
import java.util.UUID;

public record EnterpriseDemandDetail(
        EnterpriseType enterpriseType,
        Money annualRevenue,
        Money existingAnnualDebtService,
        Money netAnnualResult,
        Money guaranteesValue,
        Long enterpriseId,
        UUID enterprisePublicId,
        String sector,
        Integer companyAgeYears
) implements DemandDetail {
}
