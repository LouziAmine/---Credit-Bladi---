package com.credit.bladi.model.workflow;

import com.credit.bladi.model.enums.KeepOrManualOption;

public record DurationDecision(KeepOrManualOption option, Integer previousDuration, Integer appliedDuration) {
}
