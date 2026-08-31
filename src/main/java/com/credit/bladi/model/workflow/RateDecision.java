package com.credit.bladi.model.workflow;

import com.credit.bladi.model.enums.KeepOrManualOption;
import com.credit.bladi.model.vo.Rate;

public record RateDecision(KeepOrManualOption option, Rate previousRate, Rate appliedRate, String changedBy) {
}
