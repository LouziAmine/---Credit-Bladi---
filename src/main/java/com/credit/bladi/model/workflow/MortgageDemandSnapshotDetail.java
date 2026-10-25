package com.credit.bladi.model.workflow;

import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.model.vo.Money;

public record MortgageDemandSnapshotDetail(
        ProductType productType,
        CspCode cspCode,
        String nationalityCode,
        Boolean moroccanResident,
        Money propertyValue,
        Money monthlyIncome,
        Integer age,
        Boolean coBorrower,
        Money coBorrowerMonthlyIncome
) implements DemandSnapshotDetail {
}
