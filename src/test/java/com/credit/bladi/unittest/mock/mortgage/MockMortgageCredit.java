package com.credit.bladi.unittest.mock.mortgage;

import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1500;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_20000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_292000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_316000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_3300;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_3400;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_34000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_340000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_3500;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_5000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_500000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_600000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_7500;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_792000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_816000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_840000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_25;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_50;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_74;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_AVERAGE;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_CONVENTIONAL;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_MA;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_SALA;
import static com.credit.bladi.unittest.mock.data.MockCodes.REFERENCE_DEM_MTG12345;
import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_KARIM_BENALI_AT_EXAMPLE_MA;
import static com.credit.bladi.unittest.mock.data.MockContacts.PERSON_KARIM_BENALI;
import static com.credit.bladi.unittest.mock.data.MockContacts.PHONE_0612345678;

import com.credit.bladi.controller.dto.mortgage.request.MortgageCreditRequestDTO;
import com.credit.bladi.controller.dto.mortgage.response.ConventionalFinancingResponseDTO;
import com.credit.bladi.controller.dto.mortgage.response.MortgageCreditResponseDTO;
import com.credit.bladi.datasource.creditrate.entity.CreditRateEntity;
import com.credit.bladi.model.creditrate.CreditRateModel;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.model.enums.RateFormula;
import com.credit.bladi.model.mortgage.FinancialSnapshot;
import com.credit.bladi.model.mortgage.RateBoundsSnapshot;
import com.credit.bladi.model.mortgage.request.MortgageCreditRequest;
import com.credit.bladi.model.mortgage.response.MortgageCreditResponse;
import com.credit.bladi.model.vo.DurationRange;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import java.math.BigDecimal;

public final class MockMortgageCredit {
    public static final Long RATE_ID = 1L;
    public static final String APPLICANT_NAME = PERSON_KARIM_BENALI;
    public static final String EMAIL = EMAIL_KARIM_BENALI_AT_EXAMPLE_MA;
    public static final String PHONE = PHONE_0612345678;
    public static final String DEMAND_REFERENCE = REFERENCE_DEM_MTG12345;
    public static final BigDecimal AMOUNT = AMOUNT_500000;
    public static final BigDecimal PROPERTY_VALUE = AMOUNT_600000;
    public static final BigDecimal MONTHLY_INCOME = AMOUNT_20000;
    public static final Integer DURATION = 240;
    public static final String PRODUCT_TYPE = CODE_CONVENTIONAL;
    public static final String CSP_CODE = CODE_SALA;
    public static final String NATIONALITY_CODE = CODE_MA;
    public static final Boolean MOROCCAN_RESIDENT = true;
    public static final BigDecimal RATE_MIN = DECIMAL_5_00;
    public static final BigDecimal RATE_MAX = DECIMAL_5_50;
    public static final String RATE_FORMULA = CODE_AVERAGE;
    public static final BigDecimal APPLICABLE_RATE = DECIMAL_5_25;
    public static final Integer MIN_DURATION = 12;
    public static final Integer MAX_DURATION = 240;

    public static final Integer AGE = 40;
    public static final BigDecimal MONTHLY_PAYMENT = AMOUNT_3400;
    public static final BigDecimal ADI_MONTHLY_PREMIUM = BigDecimal.ZERO;
    public static final BigDecimal ALL_IN_MONTHLY_PAYMENT = MONTHLY_PAYMENT;
    public static final BigDecimal TOTAL_INTEREST = AMOUNT_316000;
    public static final BigDecimal TOTAL_ADI_COST = BigDecimal.ZERO;
    public static final BigDecimal TOTAL_COST = AMOUNT_816000;
    public static final BigDecimal TAEG = DECIMAL_5_74;
    public static final BigDecimal MONTHLY_PAYMENT_MIN = AMOUNT_3300;
    public static final BigDecimal MONTHLY_PAYMENT_MAX = AMOUNT_3500;
    public static final BigDecimal ADI_MONTHLY_PREMIUM_MIN = BigDecimal.ZERO;
    public static final BigDecimal ADI_MONTHLY_PREMIUM_MAX = BigDecimal.ZERO;
    public static final BigDecimal ALL_IN_MONTHLY_PAYMENT_MIN = MONTHLY_PAYMENT_MIN;
    public static final BigDecimal ALL_IN_MONTHLY_PAYMENT_MAX = MONTHLY_PAYMENT_MAX;
    public static final BigDecimal TOTAL_INTEREST_MIN = AMOUNT_292000;
    public static final BigDecimal TOTAL_INTEREST_MAX = AMOUNT_340000;
    public static final BigDecimal TOTAL_ADI_COST_MIN = BigDecimal.ZERO;
    public static final BigDecimal TOTAL_ADI_COST_MAX = BigDecimal.ZERO;
    public static final BigDecimal TOTAL_COST_MIN = AMOUNT_792000;
    public static final BigDecimal TOTAL_COST_MAX = AMOUNT_840000;
    public static final BigDecimal REGISTRATION_FEES = AMOUNT_20000;
    public static final BigDecimal LAND_CONSERVATION_FEES = AMOUNT_7500;
    public static final BigDecimal NOTARY_FEES = AMOUNT_5000;
    public static final BigDecimal PROCESSING_FEES = AMOUNT_1500;
    public static final BigDecimal TOTAL_FEES = AMOUNT_34000;

    private MockMortgageCredit() {}

    public static CreditRateEntity creditRateEntity() {
        CreditRateEntity entity = new CreditRateEntity();
        entity.setId(RATE_ID);
        entity.setCspCode(CSP_CODE);
        entity.setRateMin(RATE_MIN);
        entity.setRateMax(RATE_MAX);
        entity.setRateFormula(RATE_FORMULA);
        entity.setMinDurationMonths(MIN_DURATION);
        entity.setMaxDurationMonths(MAX_DURATION);
        entity.setActive(true);
        return entity;
    }

    public static CreditRateModel creditRateModel() {
        return CreditRateModel.getBuilder()
            .id(RATE_ID)
            .cspCode(CspCode.valueOf(CSP_CODE))
            .rateMin(Rate.of(RATE_MIN))
            .rateMax(Rate.of(RATE_MAX))
            .rateFormula(RateFormula.valueOf(RATE_FORMULA))
            .durationRange(DurationRange.of(MIN_DURATION, MAX_DURATION))
            .active(true)
            .build();
    }

    public static MortgageCreditRequestDTO requestDTO() {
        return new MortgageCreditRequestDTO(
            APPLICANT_NAME, EMAIL, PHONE,
            AMOUNT, DURATION, CSP_CODE, null, NATIONALITY_CODE, MOROCCAN_RESIDENT, PROPERTY_VALUE,
            MONTHLY_INCOME, null, null, null);
    }

    public static MortgageCreditRequest request() {
        return MortgageCreditRequest.getBuilder()
            .applicantName(APPLICANT_NAME)
            .email(EMAIL)
            .phone(PHONE)
            .amount(Money.of(AMOUNT))
            .duration(DURATION)
            .cspCode(CspCode.SALA)
            .nationalityCode(NATIONALITY_CODE)
            .moroccanResident(MOROCCAN_RESIDENT)
            .propertyValue(Money.of(PROPERTY_VALUE))
            .monthlyIncome(Money.of(MONTHLY_INCOME))
            .build();
    }

    public static MortgageCreditResponse response() {
        FinancialSnapshot applicable = new FinancialSnapshot(
            Money.of(MONTHLY_PAYMENT),
            Money.of(ADI_MONTHLY_PREMIUM),
            Money.of(ALL_IN_MONTHLY_PAYMENT),
            Money.of(TOTAL_INTEREST),
            Money.of(TOTAL_ADI_COST),
            Money.of(TOTAL_COST),
            Money.of(REGISTRATION_FEES),
            Money.of(LAND_CONSERVATION_FEES),
            Money.of(NOTARY_FEES),
            Money.of(PROCESSING_FEES),
            Money.of(TOTAL_FEES)
        );
        FinancialSnapshot min = new FinancialSnapshot(
            Money.of(MONTHLY_PAYMENT_MIN),
            Money.of(ADI_MONTHLY_PREMIUM_MIN),
            Money.of(ALL_IN_MONTHLY_PAYMENT_MIN),
            Money.of(TOTAL_INTEREST_MIN),
            Money.of(TOTAL_ADI_COST_MIN),
            Money.of(TOTAL_COST_MIN),
            Money.of(REGISTRATION_FEES),
            Money.of(LAND_CONSERVATION_FEES),
            Money.of(NOTARY_FEES),
            Money.of(PROCESSING_FEES),
            Money.of(TOTAL_FEES)
        );
        FinancialSnapshot max = new FinancialSnapshot(
            Money.of(MONTHLY_PAYMENT_MAX),
            Money.of(ADI_MONTHLY_PREMIUM_MAX),
            Money.of(ALL_IN_MONTHLY_PAYMENT_MAX),
            Money.of(TOTAL_INTEREST_MAX),
            Money.of(TOTAL_ADI_COST_MAX),
            Money.of(TOTAL_COST_MAX),
            Money.of(REGISTRATION_FEES),
            Money.of(LAND_CONSERVATION_FEES),
            Money.of(NOTARY_FEES),
            Money.of(PROCESSING_FEES),
            Money.of(TOTAL_FEES)
        );
        return MortgageCreditResponse.getBuilder()
            .amount(Money.of(AMOUNT))
            .duration(DURATION)
            .productType(ProductType.valueOf(PRODUCT_TYPE))
            .cspCode(CspCode.valueOf(CSP_CODE))
            .nationalityCode(NATIONALITY_CODE)
            .moroccanResident(MOROCCAN_RESIDENT)
            .age(AGE)
            .totalMonthlyIncome(Money.of(MONTHLY_INCOME))
            .applicableRate(Rate.of(APPLICABLE_RATE))
            .rateMin(Rate.of(RATE_MIN))
            .rateMax(Rate.of(RATE_MAX))
            .applicable(applicable)
            .bounds(new RateBoundsSnapshot(min, max))
            .taeg(TAEG)
            .demandReference(DEMAND_REFERENCE)
            .build();
    }

    public static ConventionalFinancingResponseDTO conventionalResponseDTO() {
        return new ConventionalFinancingResponseDTO(
            RATE_MIN,
            RATE_MAX,
            MONTHLY_PAYMENT,
            ADI_MONTHLY_PREMIUM,
            ALL_IN_MONTHLY_PAYMENT,
            TOTAL_INTEREST,
            TOTAL_ADI_COST,
            TOTAL_COST,
            MONTHLY_PAYMENT_MIN,
            MONTHLY_PAYMENT_MAX,
            ADI_MONTHLY_PREMIUM_MIN,
            ADI_MONTHLY_PREMIUM_MAX,
            ALL_IN_MONTHLY_PAYMENT_MIN,
            ALL_IN_MONTHLY_PAYMENT_MAX,
            TOTAL_INTEREST_MIN,
            TOTAL_INTEREST_MAX,
            TOTAL_ADI_COST_MIN,
            TOTAL_ADI_COST_MAX,
            TOTAL_COST_MIN,
            TOTAL_COST_MAX);
    }

    public static MortgageCreditResponseDTO responseDTO() {
        return new MortgageCreditResponseDTO(
            AMOUNT,
            DURATION,
            CSP_CODE,
            PRODUCT_TYPE,
            NATIONALITY_CODE,
            MOROCCAN_RESIDENT,
            AGE,
            MONTHLY_INCOME,
            APPLICABLE_RATE,
            TAEG,
            REGISTRATION_FEES,
            LAND_CONSERVATION_FEES,
            NOTARY_FEES,
            PROCESSING_FEES,
            TOTAL_FEES,
            conventionalResponseDTO(),
            null,
            null,
            DEMAND_REFERENCE);
    }
}
