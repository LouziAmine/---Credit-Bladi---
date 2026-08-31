package com.credit.bladi.batch.demand;

import com.credit.bladi.model.workflow.DemandSnapshot;

public record DemandImportItem(int rowNumber, String companyName, DemandSnapshot snapshot) {
}
