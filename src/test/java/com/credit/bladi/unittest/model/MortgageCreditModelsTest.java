package com.credit.bladi.unittest.model;

import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_168000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_20000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_25000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_2800;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_3200;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_5000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_500000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_572000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_600000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_668000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_700000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_72000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_7500;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_9000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_3_50;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_4_80;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_50;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_240;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_500000;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_FR;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_MA;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_SALA;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_OTHER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.mortgage.FinancialSnapshot;
import com.credit.bladi.model.mortgage.RateBoundsSnapshot;
import com.credit.bladi.model.mortgage.request.MortgageCreditRequest;
import com.credit.bladi.model.mortgage.response.MortgageCreditResponse;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class MortgageCreditModelsTest {
    @Test
    void shouldBuildSimulationRequest() {
        MortgageCreditRequest req = MortgageCreditRequest.getBuilder()
            .amount(Money.of(AMOUNT_500000))
            .duration(240)
            .cspCode(CspCode.SALA)
            .nationalityCode(CODE_MA)
            .moroccanResident(true)
            .propertyValue(Money.of(AMOUNT_600000))
            .monthlyIncome(Money.of(AMOUNT_20000))
            .build();

        assertNotNull(req);
        assertEquals(0, req.getAmount().amount().compareTo(AMOUNT_500000));
        assertEquals(240, req.getDuration());
        assertEquals(CspCode.SALA, req.getCspCode());
        assertEquals(CODE_MA, req.getNationalityCode());
        assertTrue(req.getMoroccanResident());
        assertEquals(0, req.getPropertyValue().amount().compareTo(AMOUNT_600000));
        assertEquals(0, req.getMonthlyIncome().amount().compareTo(AMOUNT_20000));
    }

    @Test
    void shouldSimulationRequestEqualsAndHashCode() {
        MortgageCreditRequest r1 = MortgageCreditRequest.getBuilder().duration(240).nationalityCode(CODE_MA).build();
        MortgageCreditRequest r2 = MortgageCreditRequest.getBuilder().duration(240).nationalityCode(CODE_MA).build();
        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void shouldSimulationRequestToString() {
        MortgageCreditRequest req = MortgageCreditRequest.getBuilder().duration(240).cspCode(CspCode.SALA).build();
        String str = req.toString();
        assertTrue(str.contains(TEXT_240));
    }

    @Test
    void shouldRejectNonPositiveDuration() {
        assertThrows(IllegalArgumentException.class, () -> MortgageCreditRequest.getBuilder().duration(0).build());
    }

    @Test
    void shouldRejectNonPositiveAge() {
        assertThrows(IllegalArgumentException.class, () -> MortgageCreditRequest.getBuilder().age(0).build());
    }

    @Test
    void shouldSimulationRequestBeEqualToItself() {
        MortgageCreditRequest req = MortgageCreditRequest.getBuilder().duration(240).build();
        assertTrue(req.equals(req));
    }

    @Test
    void shouldSimulationRequestNotBeEqualToDifferentType() {
        MortgageCreditRequest req = MortgageCreditRequest.getBuilder().duration(240).build();
        assertFalse(req.equals(USER_OTHER));
    }

    private static MortgageCreditRequest.Builder fullRequestBuilder() {
        return MortgageCreditRequest.getBuilder()
            .amount(Money.of(AMOUNT_500000))
            .duration(240)
            .cspCode(CspCode.SALA)
            .nationalityCode(CODE_MA)
            .moroccanResident(true)
            .propertyValue(Money.of(AMOUNT_600000))
            .monthlyIncome(Money.of(AMOUNT_20000))
            .age(40)
            .coBorrower(true)
            .coBorrowerMonthlyIncome(Money.of(AMOUNT_5000));
    }

    @Test
    void shouldBeEqualWhenAllFieldsMatch() {
        assertEquals(fullRequestBuilder().build(), fullRequestBuilder().build());
    }

    @Test
    void shouldBeEqualWhenAllOptionalFieldsAreNull() {
        MortgageCreditRequest r1 = MortgageCreditRequest.getBuilder().build();
        MortgageCreditRequest r2 = MortgageCreditRequest.getBuilder().build();

        assertEquals(r1, r2);
    }

    @Test
    void shouldNotBeEqualWhenDurationIsNullOnOneSide() {
        assertFalse(fullRequestBuilder().build().equals(fullRequestBuilder().duration(null).build()));
    }

    @Test
    void shouldNotBeEqualWhenAmountIsNullOnOneSide() {
        assertFalse(fullRequestBuilder().amount(null).build().equals(fullRequestBuilder().build()));
    }

    @Test
    void shouldNotBeEqualWhenProductTypeDiffers() {
        assertFalse(fullRequestBuilder().productType(com.credit.bladi.model.enums.ProductType.CONVENTIONAL).build()
            .equals(fullRequestBuilder().productType(com.credit.bladi.model.enums.ProductType.MOURABAHA).build()));
    }

    @Test
    void shouldNotBeEqualWhenCspCodeDiffers() {
        assertFalse(fullRequestBuilder().build().equals(fullRequestBuilder().cspCode(CspCode.FONC).build()));
    }

    @Test
    void shouldNotBeEqualWhenAmountDiffers() {
        assertFalse(fullRequestBuilder().build()
            .equals(fullRequestBuilder().amount(Money.of(AMOUNT_600000)).build()));
    }

    @Test
    void shouldNotBeEqualWhenDurationDiffers() {
        assertFalse(fullRequestBuilder().build().equals(fullRequestBuilder().duration(120).build()));
    }

    @Test
    void shouldNotBeEqualWhenNationalityCodeDiffers() {
        assertFalse(fullRequestBuilder().build().equals(fullRequestBuilder().nationalityCode(CODE_FR).build()));
    }

    @Test
    void shouldNotBeEqualWhenMoroccanResidentDiffers() {
        assertFalse(fullRequestBuilder().build().equals(fullRequestBuilder().moroccanResident(false).build()));
    }

    @Test
    void shouldNotBeEqualWhenPropertyValueDiffers() {
        assertFalse(fullRequestBuilder().build()
            .equals(fullRequestBuilder().propertyValue(Money.of(AMOUNT_700000)).build()));
    }

    @Test
    void shouldNotBeEqualWhenMonthlyIncomeDiffers() {
        assertFalse(fullRequestBuilder().build()
            .equals(fullRequestBuilder().monthlyIncome(Money.of(AMOUNT_25000)).build()));
    }

    @Test
    void shouldNotBeEqualWhenAgeDiffers() {
        assertFalse(fullRequestBuilder().build().equals(fullRequestBuilder().age(50).build()));
    }

    @Test
    void shouldNotBeEqualWhenCoBorrowerDiffers() {
        assertFalse(fullRequestBuilder().build().equals(fullRequestBuilder().coBorrower(false).build()));
    }

    @Test
    void shouldNotBeEqualWhenCoBorrowerMonthlyIncomeDiffers() {
        assertFalse(fullRequestBuilder().build()
            .equals(fullRequestBuilder().coBorrowerMonthlyIncome(Money.of(AMOUNT_9000)).build()));
    }

    @Test
    void shouldTreatNullCoBorrowerAsFalse() {
        MortgageCreditRequest req = MortgageCreditRequest.getBuilder().coBorrower(null).build();
        assertFalse(req.getCoBorrower());
    }

    @Test
    void shouldTreatExplicitFalseCoBorrowerAsFalse() {
        MortgageCreditRequest req = MortgageCreditRequest.getBuilder().coBorrower(false).build();
        assertFalse(req.getCoBorrower());
    }

    @Test
    void shouldTreatExplicitTrueCoBorrowerAsTrue() {
        MortgageCreditRequest req = MortgageCreditRequest.getBuilder().coBorrower(true).build();
        assertTrue(req.getCoBorrower());
    }

    @Test
    void shouldCreateFinancialSnapshot() {
        Money zero = Money.of(BigDecimal.ZERO);
        FinancialSnapshot snap = new FinancialSnapshot(
            zero, zero, zero, zero, zero, zero, zero, zero, zero, zero, zero);
        assertNotNull(snap);
        assertNotNull(snap.monthlyPayment());
    }

    @Test
    void shouldCreateRateBoundsSnapshot() {
        Money zero = Money.of(BigDecimal.ZERO);
        FinancialSnapshot min = new FinancialSnapshot(
            zero, zero, zero, zero, zero, zero, zero, zero, zero, zero, zero);
        FinancialSnapshot max = new FinancialSnapshot(
            zero, zero, zero, zero, zero, zero, zero, zero, zero, zero, zero);
        RateBoundsSnapshot snap = new RateBoundsSnapshot(min, max);
        assertNotNull(snap);
        assertNotNull(snap.min());
        assertNotNull(snap.max());
    }

    @Test
    void shouldBuildSimulationResponseWithAllFields() {
        Money zero = Money.of(BigDecimal.ZERO);
        FinancialSnapshot min = new FinancialSnapshot(
            Money.of(AMOUNT_2800), zero, Money.of(AMOUNT_2800),
            Money.of(AMOUNT_72000), zero, Money.of(AMOUNT_572000),
            Money.of(AMOUNT_7500), Money.of(AMOUNT_7500),
            Money.of(AMOUNT_5000), Money.of(AMOUNT_5000), Money.of(AMOUNT_25000));
        FinancialSnapshot max = new FinancialSnapshot(
            Money.of(AMOUNT_3200), zero, Money.of(AMOUNT_3200),
            Money.of(AMOUNT_168000), zero, Money.of(AMOUNT_668000),
            Money.of(AMOUNT_7500), Money.of(AMOUNT_7500),
            Money.of(AMOUNT_5000), Money.of(AMOUNT_5000), Money.of(AMOUNT_25000));

        MortgageCreditResponse resp = MortgageCreditResponse.getBuilder()
            .amount(Money.of(AMOUNT_500000))
            .duration(240)
            .cspCode(CspCode.SALA)
            .nationalityCode(CODE_MA)
            .moroccanResident(true)
            .applicableRate(Rate.of(DECIMAL_4_80))
            .rateMin(Rate.of(DECIMAL_3_50))
            .rateMax(Rate.of(DECIMAL_5_50))
            .applicable(min)
            .bounds(new RateBoundsSnapshot(min, max))
            .build();

        assertNotNull(resp);
        assertEquals(0, resp.amount().amount().compareTo(AMOUNT_500000));
        assertEquals(240, resp.duration());
        assertEquals(CspCode.SALA, resp.cspCode());
        assertEquals(CODE_MA, resp.nationalityCode());
        assertTrue(resp.moroccanResident());
        assertEquals(0, resp.applicableRate().value().compareTo(DECIMAL_4_80));
        assertEquals(0, resp.bounds().min().registrationFees().amount().compareTo(AMOUNT_7500));
    }

    @Test
    void shouldSimulationResponseEqualsAndHashCode() {
        MortgageCreditResponse r1 = MortgageCreditResponse.getBuilder()
            .amount(Money.of(AMOUNT_500000)).build();
        MortgageCreditResponse r2 = MortgageCreditResponse.getBuilder()
            .amount(Money.of(AMOUNT_500000)).build();
        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void shouldSimulationResponseToString() {
        MortgageCreditResponse resp = MortgageCreditResponse.getBuilder()
            .amount(Money.of(AMOUNT_500000))
            .cspCode(CspCode.SALA)
            .build();
        String str = resp.toString();
        assertTrue(str.contains(TEXT_500000));
        assertTrue(str.contains(CODE_SALA));
    }
}
