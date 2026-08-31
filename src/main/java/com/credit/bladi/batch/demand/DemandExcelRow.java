package com.credit.bladi.batch.demand;

import com.credit.bladi.model.enums.EnterpriseType;
import java.math.BigDecimal;

public record DemandExcelRow(
    int rowNumber,
    String companyName,
    EnterpriseType enterpriseType,
    BigDecimal amount,
    Integer duration,
    BigDecimal applicableRate,
    String contactEmail,
    String contactPhone,
    BigDecimal annualRevenue,
    BigDecimal existingAnnualDebtService,
    BigDecimal netAnnualResult,
    BigDecimal guaranteesValue,
    String sector,
    Integer companyAgeYears) {
}
