package com.credit.bladi.batch.demand;

import com.credit.bladi.batch.excel.ImportRowException;
import com.credit.bladi.batch.excel.RowFieldParser;
import com.credit.bladi.batch.excel.RowMapper;
import com.credit.bladi.model.enums.EnterpriseType;
import java.math.BigDecimal;
import java.util.Locale;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.context.MessageSource;

public class DemandRowMapper implements RowMapper<DemandExcelRow> {
    private static final int COL_COMPANY_NAME = 0;
    private static final int COL_ENTERPRISE_TYPE = 1;
    private static final int COL_AMOUNT = 2;
    private static final int COL_DURATION = 3;
    private static final int COL_APPLICABLE_RATE = 4;
    private static final int COL_CONTACT_EMAIL = 5;
    private static final int COL_CONTACT_PHONE = 6;
    private static final int COL_ANNUAL_REVENUE = 7;
    private static final int COL_EXISTING_ANNUAL_DEBT_SERVICE = 8;
    private static final int COL_NET_ANNUAL_RESULT = 9;
    private static final int COL_GUARANTEES_VALUE = 10;
    private static final int COL_SECTOR = 11;
    private static final int COL_COMPANY_AGE_YEARS = 12;

    private final MessageSource messageSource;

    private final RowFieldParser fieldParser;

    public DemandRowMapper(MessageSource messageSource) {
        this.messageSource = messageSource;
        this.fieldParser = new RowFieldParser(messageSource);
    }

    private String message(String key, Object... args) {
        return messageSource.getMessage(key, args, Locale.FRENCH);
    }

    @Override
    public DemandExcelRow map(Row row, int rowNumber) throws ImportRowException {
        String companyName = fieldParser.requiredString(row, rowNumber, COL_COMPANY_NAME, "companyName");
        EnterpriseType enterpriseType = requiredEnterpriseType(row, rowNumber);
        BigDecimal amount = fieldParser.requiredDecimal(row, rowNumber, COL_AMOUNT, "amount");
        Integer duration = fieldParser.requiredInt(row, rowNumber, COL_DURATION, "duration");
        BigDecimal applicableRate = fieldParser.optionalDecimal(row, rowNumber, COL_APPLICABLE_RATE);
        String contactEmail = fieldParser.requiredEmail(row, rowNumber, COL_CONTACT_EMAIL, "contactEmail");
        String contactPhone = fieldParser.requiredString(row, rowNumber, COL_CONTACT_PHONE, "contactPhone");
        BigDecimal annualRevenue = fieldParser.optionalDecimal(row, rowNumber, COL_ANNUAL_REVENUE);
        BigDecimal existingAnnualDebtService =
            fieldParser.optionalDecimal(row, rowNumber, COL_EXISTING_ANNUAL_DEBT_SERVICE);
        BigDecimal netAnnualResult = fieldParser.optionalDecimal(row, rowNumber, COL_NET_ANNUAL_RESULT);
        BigDecimal guaranteesValue = fieldParser.optionalDecimal(row, rowNumber, COL_GUARANTEES_VALUE);
        String sector = fieldParser.optionalString(row, rowNumber, COL_SECTOR);
        Integer companyAgeYears = fieldParser.optionalInt(row, rowNumber, COL_COMPANY_AGE_YEARS, "companyAgeYears");

        return new DemandExcelRow(rowNumber, companyName, enterpriseType, amount, duration, applicableRate,
            contactEmail, contactPhone, annualRevenue, existingAnnualDebtService, netAnnualResult, guaranteesValue,
            sector, companyAgeYears);
    }

    private EnterpriseType requiredEnterpriseType(Row row, int rowNumber) {
        String raw = fieldParser.requiredString(row, rowNumber, COL_ENTERPRISE_TYPE, "enterpriseType");
        try {
            return EnterpriseType.valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ImportRowException(rowNumber, message("demandImport.enterpriseTypeInvalid", raw));
        }
    }
}
