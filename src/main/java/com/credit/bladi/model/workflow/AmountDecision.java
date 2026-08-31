package com.credit.bladi.model.workflow;

import com.credit.bladi.model.enums.KeepOrManualOption;
import com.credit.bladi.model.vo.Money;

public record AmountDecision(KeepOrManualOption option, Money previousAmount, Money appliedAmount) {
}
