package com.credit.bladi.model.vo;

public record AmortizationRow(
        int installmentNumber,
        Money beginningBalance,
        Money interest,
        Money principal,
        Money payment,
        Money endingBalance
) {
}
