package com.credit.bladi.unittest.batch.demand;

import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_150000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_2000000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_3000000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_50000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_500000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_600000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_6_5;
import static com.credit.bladi.unittest.mock.data.MockAmounts.MINUS_AMOUNT_1;
import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_ATLAS_NEGOCE;
import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_NEGATIVE_CORP;
import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_SAHARA_INDUSTRIES;
import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_A_AT_B_MA;
import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_CONTACT_AT_ATLAS_MA;
import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_FINANCE_AT_SAHARA_MA;
import static com.credit.bladi.unittest.mock.data.MockContacts.PHONE_0522334455;
import static com.credit.bladi.unittest.mock.data.MockContacts.PHONE_0522998877;
import static com.credit.bladi.unittest.mock.data.MockContacts.PHONE_0600000000;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_COMMERCE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.credit.bladi.batch.demand.DemandExcelRow;
import com.credit.bladi.batch.demand.DemandImportItem;
import com.credit.bladi.batch.demand.DemandSnapshotProcessor;
import com.credit.bladi.batch.excel.ImportRowException;
import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.model.workflow.DemandSnapshot;
import com.credit.bladi.model.workflow.EnterpriseDemandSnapshotDetail;
import org.junit.jupiter.api.Test;

class DemandSnapshotProcessorTest {
    private final DemandSnapshotProcessor processor = new DemandSnapshotProcessor();

    @Test
    void buildsASnapshotFromAFullyPopulatedRow() {
        DemandExcelRow row = new DemandExcelRow(2, COMPANY_ATLAS_NEGOCE, EnterpriseType.PME,
            AMOUNT_500000, 60, DECIMAL_6_5, EMAIL_CONTACT_AT_ATLAS_MA, PHONE_0522334455,
            AMOUNT_2000000, AMOUNT_50000, AMOUNT_150000,
            AMOUNT_600000, LABEL_COMMERCE, 8);

        DemandImportItem item = processor.process(row);

        assertEquals(2, item.rowNumber());
        assertEquals(COMPANY_ATLAS_NEGOCE, item.companyName());
        DemandSnapshot snapshot = item.snapshot();
        assertEquals(COMPANY_ATLAS_NEGOCE, snapshot.contactName());
        assertEquals(EnterpriseType.PME, ((EnterpriseDemandSnapshotDetail) snapshot.detail()).enterpriseType());
        assertEquals(0, AMOUNT_500000.compareTo(snapshot.amount().amount()));
        assertEquals(60, snapshot.duration());
        assertEquals(0, DECIMAL_6_5.compareTo(snapshot.applicableRate().value()));
    }

    @Test
    void leavesOptionalMoneyAndRateNullWhenTheRowLeftThemNull() {
        DemandExcelRow row = new DemandExcelRow(3, COMPANY_SAHARA_INDUSTRIES, EnterpriseType.GE,
            AMOUNT_3000000, 84, null, EMAIL_FINANCE_AT_SAHARA_MA, PHONE_0522998877,
            null, null, null, null, null, null);

        DemandImportItem item = processor.process(row);

        assertNull(item.snapshot().applicableRate());
        assertNull(((EnterpriseDemandSnapshotDetail) item.snapshot().detail()).annualRevenue());
    }

    @Test
    void wrapsANegativeAmountAsAnImportRowException() {
        DemandExcelRow row = new DemandExcelRow(4, COMPANY_NEGATIVE_CORP, EnterpriseType.TPE,
            MINUS_AMOUNT_1, 12, null, EMAIL_A_AT_B_MA, PHONE_0600000000, null, null, null, null, null, null);

        ImportRowException exception = assertThrows(ImportRowException.class, () -> processor.process(row));

        assertEquals(4, exception.getRowNumber());
    }
}
