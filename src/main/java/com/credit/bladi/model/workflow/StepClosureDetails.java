package com.credit.bladi.model.workflow;

import static com.credit.bladi.exception.WorkflowException.WorkflowExceptionType.MANUAL_AMOUNT_REQUIRED;
import static com.credit.bladi.exception.WorkflowException.WorkflowExceptionType.MANUAL_DURATION_REQUIRED;
import static com.credit.bladi.exception.WorkflowException.WorkflowExceptionType.MANUAL_RATE_REQUIRED;
import static com.credit.bladi.exception.WorkflowException.WorkflowExceptionType.RATE_OPTION_KEEP_UNAVAILABLE;

import com.credit.bladi.exception.WorkflowException;
import com.credit.bladi.model.enums.CommitteeType;
import com.credit.bladi.model.enums.DecisionOutcome;
import com.credit.bladi.model.enums.InsuranceCoverageType;
import com.credit.bladi.model.enums.KeepOrManualOption;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import java.math.BigDecimal;

public record StepClosureDetails(
        String note,
        DecisionOutcome decisionOutcome,
        String returnReasonCode,
        String recommendations,
        String reserves,
        CommitteeType committeeType,
        Boolean insuranceAdiSubscribed,
        InsuranceCoverageType insuranceCoverageType,
        Boolean insuranceDamageSubscribed,
        KeepOrManualOption rateOption,
        BigDecimal manualRate,
        KeepOrManualOption amountOption,
        BigDecimal manualAmount,
        KeepOrManualOption durationOption,
        Integer manualDuration
) {
    public boolean isReturnForCompletion() {
        return returnReasonCode != null;
    }

    public boolean isRefusal() {
        return !isReturnForCompletion() && decisionOutcome == DecisionOutcome.REJECTED;
    }

    public boolean isDeferralMovingForward() {
        return !isReturnForCompletion() && decisionOutcome == DecisionOutcome.DEFERRED;
    }

    public boolean overridesTerms() {
        return rateOption == KeepOrManualOption.MANUAL || amountOption == KeepOrManualOption.MANUAL
            || durationOption == KeepOrManualOption.MANUAL;
    }

    public RateDecision rateDecision(Rate previousRate, String decidedBy) {
        Rate appliedRate = switch (rateOption) {
            case KEEP -> {
                if (previousRate == null) {
                    throw WorkflowException.of(RATE_OPTION_KEEP_UNAVAILABLE);
                }
                yield previousRate;
            }
            case MANUAL -> {
                if (manualRate == null) {
                    throw WorkflowException.of(MANUAL_RATE_REQUIRED);
                }
                yield new Rate(manualRate);
            }
        };
        return new RateDecision(rateOption, previousRate, appliedRate, decidedBy);
    }

    public AmountDecision amountDecision(Money previousAmount) {
        if (amountOption != KeepOrManualOption.MANUAL) {
            return new AmountDecision(amountOption, previousAmount, previousAmount);
        }
        if (manualAmount == null) {
            throw WorkflowException.of(MANUAL_AMOUNT_REQUIRED);
        }
        return new AmountDecision(amountOption, previousAmount, new Money(manualAmount));
    }

    public DurationDecision durationDecision(Integer previousDuration) {
        if (durationOption != KeepOrManualOption.MANUAL) {
            return new DurationDecision(durationOption, previousDuration, previousDuration);
        }
        if (manualDuration == null) {
            throw WorkflowException.of(MANUAL_DURATION_REQUIRED);
        }
        return new DurationDecision(durationOption, previousDuration, manualDuration);
    }
}
