package com.credit.bladi.model.mortgage;

import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.model.mourabaha.MourabahaAmortizationRow;
import com.credit.bladi.model.mourabaha.MourabahaFinancialSnapshot;
import com.credit.bladi.model.vo.AmortizationRow;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.workflow.DemandAmortizationSnapshot;
import java.math.BigDecimal;
import java.util.List;

public record MortgageAmortizationSnapshot(
        ProductType productType,
        Rate applicableRate,
        BigDecimal taeg,
        FinancialSnapshot conventional,
        List<AmortizationRow> amortizationSchedule,
        MourabahaFinancialSnapshot mourabaha,
        List<MourabahaAmortizationRow> mourabahaAmortizationSchedule
) implements DemandAmortizationSnapshot {
}
