package com.credit.bladi.unittest.mock.workflow;

import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_MA;

import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.DemandType;
import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.model.workflow.DemandSnapshot;
import com.credit.bladi.model.workflow.EnterpriseDemandDetail;
import com.credit.bladi.model.workflow.EnterpriseDemandSnapshotDetail;
import com.credit.bladi.model.workflow.MortgageDemandDetail;
import java.time.Instant;
import java.util.UUID;

public final class MockDemand {
    private MockDemand() {
    }

    public static DemandModel enterprise(Long id, UUID publicId, String reference, Long workflowId,
            String companyName, EnterpriseType enterpriseType, Money amount, Integer duration, Instant createdAt,
            Instant completedAt, Rate applicableRate, String contactEmail, String contactPhone, Money annualRevenue,
            Money existingAnnualDebtService, Money netAnnualResult, Money guaranteesValue, Long enterpriseId,
            Instant rejectedAt, String sector, Integer companyAgeYears) {
        return new DemandModel(id, publicId, reference, workflowId, DemandType.ENTERPRISE, companyName, amount,
            duration, createdAt, completedAt, applicableRate, contactEmail, contactPhone, null, rejectedAt,
            new EnterpriseDemandDetail(enterpriseType, annualRevenue, existingAnnualDebtService, netAnnualResult,
                guaranteesValue, enterpriseId, null, sector, companyAgeYears));
    }

    public static DemandModel mortgage(Long id, UUID publicId, String reference, Long workflowId,
            String applicantName, Money amount, Integer duration, Instant createdAt, Instant completedAt,
            Rate applicableRate, String contactEmail, String contactPhone, Instant rejectedAt,
            ProductType productType, CspCode cspCode, Money propertyValue, Money monthlyIncome, Integer age,
            Boolean coBorrower, Money coBorrowerMonthlyIncome) {
        return new DemandModel(id, publicId, reference, workflowId, DemandType.MORTGAGE, applicantName, amount,
            duration, createdAt, completedAt, applicableRate, contactEmail, contactPhone, null, rejectedAt,
            new MortgageDemandDetail(productType, cspCode, CODE_MA, true, propertyValue, monthlyIncome, age, coBorrower,
                coBorrowerMonthlyIncome));
    }

    public static DemandSnapshot enterpriseSnapshot(String companyName, EnterpriseType enterpriseType, Money amount,
            Integer duration, Rate applicableRate, String contactEmail, String contactPhone, Money annualRevenue,
            Money existingAnnualDebtService, Money netAnnualResult, Money guaranteesValue, String sector,
            Integer companyAgeYears) {
        return new DemandSnapshot(DemandType.ENTERPRISE, companyName, amount, duration, applicableRate, contactEmail,
            contactPhone, null, new EnterpriseDemandSnapshotDetail(enterpriseType, annualRevenue,
                existingAnnualDebtService,
                netAnnualResult, guaranteesValue, sector, companyAgeYears));
    }
}
