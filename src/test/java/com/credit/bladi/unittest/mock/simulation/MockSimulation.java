package com.credit.bladi.unittest.mock.simulation;

import com.credit.bladi.controller.dto.simulation.request.SimulationRequestDTO;
import com.credit.bladi.controller.dto.simulation.response.SimulationResponseDTO;
import com.credit.bladi.datasource.creditrate.entity.CreditRateEntity;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.model.simulation.CreditRateModel;
import com.credit.bladi.model.simulation.FinancialSnapshot;
import com.credit.bladi.model.simulation.RateBoundsSnapshot;
import com.credit.bladi.model.simulation.request.SimulationRequest;
import com.credit.bladi.model.simulation.response.SimulationResponse;
import com.credit.bladi.model.vo.DurationRange;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import java.math.BigDecimal;

public final class MockSimulation {

    public static final Long RATE_ID = 1L;
    public static final BigDecimal AMOUNT = new BigDecimal("500000");
    public static final BigDecimal PROPERTY_VALUE = new BigDecimal("600000");
    public static final BigDecimal MONTHLY_INCOME = new BigDecimal("20000");
    public static final Integer DURATION = 240;
    public static final String PRODUCT_TYPE = "CONVENTIONAL";
    public static final String CSP_CODE = "SALA";
    public static final String NATIONALITY_CODE = "MA";
    public static final Boolean MOROCCAN_RESIDENT = true;
    public static final BigDecimal RATE_MIN = new BigDecimal("5.00");
    public static final BigDecimal RATE_MAX = new BigDecimal("5.50");
    public static final BigDecimal APPLICABLE_RATE = new BigDecimal("5.25");
    public static final Integer MIN_DURATION = 12;
    public static final Integer MAX_DURATION = 240;

    public static final Integer AGE = 40;
    public static final BigDecimal MONTHLY_PAYMENT = new BigDecimal("3400");
    public static final BigDecimal ADI_MONTHLY_PREMIUM = BigDecimal.ZERO;
    public static final BigDecimal ALL_IN_MONTHLY_PAYMENT = MONTHLY_PAYMENT;
    public static final BigDecimal TOTAL_INTEREST = new BigDecimal("316000");
    public static final BigDecimal TOTAL_ADI_COST = BigDecimal.ZERO;
    public static final BigDecimal TOTAL_COST = new BigDecimal("816000");
    public static final BigDecimal TAEG = new BigDecimal("5.74");
    public static final BigDecimal MONTHLY_PAYMENT_MIN = new BigDecimal("3300");
    public static final BigDecimal MONTHLY_PAYMENT_MAX = new BigDecimal("3500");
    public static final BigDecimal ADI_MONTHLY_PREMIUM_MIN = BigDecimal.ZERO;
    public static final BigDecimal ADI_MONTHLY_PREMIUM_MAX = BigDecimal.ZERO;
    public static final BigDecimal ALL_IN_MONTHLY_PAYMENT_MIN = MONTHLY_PAYMENT_MIN;
    public static final BigDecimal ALL_IN_MONTHLY_PAYMENT_MAX = MONTHLY_PAYMENT_MAX;
    public static final BigDecimal TOTAL_INTEREST_MIN = new BigDecimal("292000");
    public static final BigDecimal TOTAL_INTEREST_MAX = new BigDecimal("340000");
    public static final BigDecimal TOTAL_ADI_COST_MIN = BigDecimal.ZERO;
    public static final BigDecimal TOTAL_ADI_COST_MAX = BigDecimal.ZERO;
    public static final BigDecimal TOTAL_COST_MIN = new BigDecimal("792000");
    public static final BigDecimal TOTAL_COST_MAX = new BigDecimal("840000");
    public static final BigDecimal REGISTRATION_FEES = new BigDecimal("20000");
    public static final BigDecimal LAND_CONSERVATION_FEES = new BigDecimal("7500");
    public static final BigDecimal NOTARY_FEES = new BigDecimal("5000");
    public static final BigDecimal PROCESSING_FEES = new BigDecimal("1500");
    public static final BigDecimal TOTAL_FEES = new BigDecimal("34000");

    private MockSimulation() {}

    public static CreditRateEntity creditRateEntity() {
        CreditRateEntity entity = new CreditRateEntity();
        entity.setId(RATE_ID);
        entity.setCspCode(CSP_CODE);
        entity.setRateMin(RATE_MIN);
        entity.setRateMax(RATE_MAX);
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
            .durationRange(DurationRange.of(MIN_DURATION, MAX_DURATION))
            .active(true)
            .build();
    }

    public static SimulationRequestDTO requestDTO() {
        SimulationRequestDTO dto = new SimulationRequestDTO();
        dto.setAmount(AMOUNT);
        dto.setDuration(DURATION);
        dto.setCspCode(CSP_CODE);
        dto.setNationalityCode(NATIONALITY_CODE);
        dto.setMoroccanResident(MOROCCAN_RESIDENT);
        dto.setPropertyValue(PROPERTY_VALUE);
        dto.setMonthlyIncome(MONTHLY_INCOME);
        return dto;
    }

    public static SimulationRequest request() {
        return SimulationRequest.getBuilder()
            .amount(Money.of(AMOUNT))
            .duration(DURATION)
            .cspCode(CspCode.SALA)
            .nationalityCode(NATIONALITY_CODE)
            .moroccanResident(MOROCCAN_RESIDENT)
            .propertyValue(Money.of(PROPERTY_VALUE))
            .monthlyIncome(Money.of(MONTHLY_INCOME))
            .build();
    }

    public static SimulationResponse response() {
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
        return SimulationResponse.getBuilder()
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
            .build();
    }

    public static SimulationResponseDTO responseDTO() {
        SimulationResponseDTO dto = new SimulationResponseDTO();
        dto.setAmount(AMOUNT);
        dto.setDuration(DURATION);
        dto.setCspCode(CSP_CODE);
        dto.setProductType(PRODUCT_TYPE);
        dto.setNationalityCode(NATIONALITY_CODE);
        dto.setMoroccanResident(MOROCCAN_RESIDENT);
        dto.setAge(AGE);
        dto.setTotalMonthlyIncome(MONTHLY_INCOME);
        dto.setApplicableRate(APPLICABLE_RATE);
        dto.setRateMin(RATE_MIN);
        dto.setRateMax(RATE_MAX);
        dto.setMonthlyPayment(MONTHLY_PAYMENT);
        dto.setAdiMonthlyPremium(ADI_MONTHLY_PREMIUM);
        dto.setAllInMonthlyPayment(ALL_IN_MONTHLY_PAYMENT);
        dto.setTotalInterest(TOTAL_INTEREST);
        dto.setTotalAdiCost(TOTAL_ADI_COST);
        dto.setTotalCost(TOTAL_COST);
        dto.setTaeg(TAEG);
        dto.setMonthlyPaymentMin(MONTHLY_PAYMENT_MIN);
        dto.setMonthlyPaymentMax(MONTHLY_PAYMENT_MAX);
        dto.setAdiMonthlyPremiumMin(ADI_MONTHLY_PREMIUM_MIN);
        dto.setAdiMonthlyPremiumMax(ADI_MONTHLY_PREMIUM_MAX);
        dto.setAllInMonthlyPaymentMin(ALL_IN_MONTHLY_PAYMENT_MIN);
        dto.setAllInMonthlyPaymentMax(ALL_IN_MONTHLY_PAYMENT_MAX);
        dto.setTotalInterestMin(TOTAL_INTEREST_MIN);
        dto.setTotalInterestMax(TOTAL_INTEREST_MAX);
        dto.setTotalAdiCostMin(TOTAL_ADI_COST_MIN);
        dto.setTotalAdiCostMax(TOTAL_ADI_COST_MAX);
        dto.setTotalCostMin(TOTAL_COST_MIN);
        dto.setTotalCostMax(TOTAL_COST_MAX);
        dto.setRegistrationFees(REGISTRATION_FEES);
        dto.setLandConservationFees(LAND_CONSERVATION_FEES);
        dto.setNotaryFees(NOTARY_FEES);
        dto.setProcessingFees(PROCESSING_FEES);
        dto.setTotalFees(TOTAL_FEES);
        return dto;
    }
}