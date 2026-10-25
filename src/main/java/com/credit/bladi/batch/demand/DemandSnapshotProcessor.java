package com.credit.bladi.batch.demand;

import static com.credit.bladi.model.vo.ValueObjectMappers.toMoney;
import static com.credit.bladi.model.vo.ValueObjectMappers.toRate;

import com.credit.bladi.batch.excel.ImportRowException;
import com.credit.bladi.model.enums.DemandType;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.workflow.DemandSnapshot;
import com.credit.bladi.model.workflow.EnterpriseDemandSnapshotDetail;
import org.springframework.batch.infrastructure.item.ItemProcessor;

public class DemandSnapshotProcessor implements ItemProcessor<DemandExcelRow, DemandImportItem> {
    @Override
    public DemandImportItem process(DemandExcelRow row) {
        try {
            DemandSnapshot snapshot = new DemandSnapshot(DemandType.ENTERPRISE, row.companyName(),
                Money.of(row.amount()), row.duration(), toRate(row.applicableRate()),
                row.contactEmail(), row.contactPhone(), null,
                new EnterpriseDemandSnapshotDetail(row.enterpriseType(),
                    toMoney(row.annualRevenue()), toMoney(row.existingAnnualDebtService()),
                    toMoney(row.netAnnualResult()), toMoney(row.guaranteesValue()),
                    row.sector(), row.companyAgeYears()));
            return new DemandImportItem(row.rowNumber(), row.companyName(), snapshot);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ImportRowException(row.rowNumber(), e.getMessage());
        }
    }
}
