package com.credit.bladi.model.mourabaha;

import com.credit.bladi.model.vo.Money;

public record MourabahaAmortizationRow(
        int installmentNumber,
        Money beginningBalance,
        Money marginHt,
        Money vatOnMargin,
        Money marginTtc,
        Money principal,
        Money payment,
        Money endingBalance
) {
}
