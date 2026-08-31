package com.credit.bladi.unittest.batch.demand;

import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_500000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_6_5;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_100000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_12;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_150000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_2000000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_24;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_3000000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_50000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_500000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_60;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_600000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_6_5;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_84;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_GE;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_NOT_A_TYPE;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_PME;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_TPE;
import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_ATLAS_NEGOCE;
import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_BROKEN_CORP;
import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_SAHARA_INDUSTRIES;
import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_WEIRD_CORP;
import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_A_AT_B_MA;
import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_BAD_AT_BROKEN_MA;
import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_CONTACT_AT_ATLAS_MA;
import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_FINANCE_AT_SAHARA_MA;
import static com.credit.bladi.unittest.mock.data.MockContacts.PHONE_0522334455;
import static com.credit.bladi.unittest.mock.data.MockContacts.PHONE_0522998877;
import static com.credit.bladi.unittest.mock.data.MockContacts.PHONE_0600000000;
import static com.credit.bladi.unittest.mock.data.MockContacts.PHONE_0611223344;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_COMMERCE;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.batch.demand.DemandExcelRow;
import com.credit.bladi.batch.demand.DemandRowMapper;
import com.credit.bladi.batch.excel.ImportRowException;
import com.credit.bladi.model.enums.EnterpriseType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;

class DemandRowMapperTest {
    private static final ResourceBundleMessageSource MESSAGE_SOURCE = new ResourceBundleMessageSource();

    static {
        MESSAGE_SOURCE.setBasenames("i18n/exception/demand-import");
        MESSAGE_SOURCE.setDefaultEncoding(LABEL_UTF_8);
    }

    private final DemandRowMapper mapper = new DemandRowMapper(MESSAGE_SOURCE);

    @Test
    void mapsAFullyPopulatedRow() {
        Row row = rowOf(COMPANY_ATLAS_NEGOCE, CODE_PME, TEXT_500000, TEXT_60, TEXT_6_5, EMAIL_CONTACT_AT_ATLAS_MA,
            PHONE_0522334455,
            TEXT_2000000, TEXT_50000, TEXT_150000, TEXT_600000, LABEL_COMMERCE, "8");

        DemandExcelRow result = mapper.map(row, 2);

        assertEquals(2, result.rowNumber());
        assertEquals(COMPANY_ATLAS_NEGOCE, result.companyName());
        assertEquals(EnterpriseType.PME, result.enterpriseType());
        assertEquals(0, AMOUNT_500000.compareTo(result.amount()));
        assertEquals(60, result.duration());
        assertEquals(0, DECIMAL_6_5.compareTo(result.applicableRate()));
        assertEquals(EMAIL_CONTACT_AT_ATLAS_MA, result.contactEmail());
        assertEquals(LABEL_COMMERCE, result.sector());
        assertEquals(8, result.companyAgeYears());
    }

    @Test
    void leavesOptionalColumnsNullWhenBlank() {
        Row row = rowOf(COMPANY_SAHARA_INDUSTRIES, CODE_GE, TEXT_3000000, TEXT_84, null, EMAIL_FINANCE_AT_SAHARA_MA,
            PHONE_0522998877,
            null, null, null, null, null, null);

        DemandExcelRow result = mapper.map(row, 3);

        assertNull(result.applicableRate());
        assertNull(result.annualRevenue());
        assertNull(result.sector());
        assertNull(result.companyAgeYears());
    }

    @Test
    void rejectsAMissingRequiredColumn() {
        Row row = rowOf(COMPANY_BROKEN_CORP, CODE_TPE, null, TEXT_24, null, EMAIL_BAD_AT_BROKEN_MA, PHONE_0611223344,
            null, null, null, null, null, null);

        ImportRowException exception = assertThrows(ImportRowException.class, () -> mapper.map(row, 4));

        assertEquals(4, exception.getRowNumber());
        assertTrue(exception.getMessage().contains("amount"));
    }

    @Test
    void rejectsAnUnknownEnterpriseType() {
        Row row = rowOf(COMPANY_WEIRD_CORP, CODE_NOT_A_TYPE, TEXT_100000, TEXT_12, null, EMAIL_A_AT_B_MA,
            PHONE_0600000000,
            null, null, null, null, null, null);

        ImportRowException exception = assertThrows(ImportRowException.class, () -> mapper.map(row, 5));

        assertTrue(exception.getMessage().contains("enterpriseType"));
    }

    @Test
    void rejectsAMalformedEmail() {
        Row row = rowOf(COMPANY_WEIRD_CORP, CODE_TPE, TEXT_100000, TEXT_12, null, "not-an-email", PHONE_0600000000,
            null, null, null, null, null, null);

        ImportRowException exception = assertThrows(ImportRowException.class, () -> mapper.map(row, 6));

        assertTrue(exception.getMessage().contains("contactEmail"));
    }

    private static Row rowOf(String... values) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        Row row = sheet.createRow(0);
        for (int i = 0; i < values.length; i++) {
            if (values[i] != null) {
                row.createCell(i).setCellValue(values[i]);
            }
        }
        return row;
    }
}
