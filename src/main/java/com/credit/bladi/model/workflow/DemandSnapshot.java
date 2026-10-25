package com.credit.bladi.model.workflow;

import com.credit.bladi.model.enums.DemandType;
import com.credit.bladi.model.enums.Language;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;

public record DemandSnapshot(DemandType demandType, String contactName, Money amount, Integer duration,
                              Rate applicableRate, String contactEmail, String contactPhone,
                              Language contactLanguage, DemandSnapshotDetail detail) {
}
