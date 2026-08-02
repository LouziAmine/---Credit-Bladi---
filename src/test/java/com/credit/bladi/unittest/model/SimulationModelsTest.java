package com.credit.bladi.unittest.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.simulation.FinancialSnapshot;
import com.credit.bladi.model.simulation.RateBoundsSnapshot;
import com.credit.bladi.model.simulation.request.SimulationRequest;
import com.credit.bladi.model.simulation.response.SimulationResponse;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class SimulationModelsTest {

    @Test
    void shouldBuildSimulationRequest() {
        SimulationRequest req = SimulationRequest.getBuilder()
            .amount(Money.of(new BigDecimal("500000")))
            .duration(240)
            .cspCode(CspCode.SALA)
            .nationalityCode("MA")
            .moroccanResident(true)
            .propertyValue(Money.of(new BigDecimal("600000")))
            .monthlyIncome(Money.of(new BigDecimal("20000")))
            .build();

        assertNotNull(req);
        assertEquals(0, req.getAmount().amount().compareTo(new BigDecimal("500000")));
        assertEquals(240, req.getDuration());
        assertEquals(CspCode.SALA, req.getCspCode());
        assertEquals("MA", req.getNationalityCode());
        assertTrue(req.getMoroccanResident());
        assertEquals(0, req.getPropertyValue().amount().compareTo(new BigDecimal("600000")));
        assertEquals(0, req.getMonthlyIncome().amount().compareTo(new BigDecimal("20000")));
    }

    @Test
    void shouldSimulationRequestEqualsAndHashCode() {
        SimulationRequest r1 = SimulationRequest.getBuilder().duration(240).nationalityCode("MA").build();
        SimulationRequest r2 = SimulationRequest.getBuilder().duration(240).nationalityCode("MA").build();
        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void shouldSimulationRequestToString() {
        SimulationRequest req = SimulationRequest.getBuilder().duration(240).cspCode(CspCode.SALA).build();
        String str = req.toString();
        assertTrue(str.contains("240"));
    }

    @Test
    void shouldRejectNonPositiveDuration() {
        assertThrows(IllegalArgumentException.class, () -> SimulationRequest.getBuilder().duration(0).build());
    }

    @Test
    void shouldRejectNonPositiveAge() {
        assertThrows(IllegalArgumentException.class, () -> SimulationRequest.getBuilder().age(0).build());
    }

    @Test
    void shouldSimulationRequestBeEqualToItself() {
        SimulationRequest req = SimulationRequest.getBuilder().duration(240).build();
        assertTrue(req.equals(req));
    }

    @Test
    void shouldSimulationRequestNotBeEqualToDifferentType() {
        SimulationRequest req = SimulationRequest.getBuilder().duration(240).build();
        assertFalse(req.equals("other"));
    }

    private static SimulationRequest.Builder fullRequestBuilder() {
        return SimulationRequest.getBuilder()
            .amount(Money.of(new BigDecimal("500000")))
            .duration(240)
            .cspCode(CspCode.SALA)
            .nationalityCode("MA")
            .moroccanResident(true)
            .propertyValue(Money.of(new BigDecimal("600000")))
            .monthlyIncome(Money.of(new BigDecimal("20000")))
            .age(40)
            .coBorrower(true)
            .coBorrowerMonthlyIncome(Money.of(new BigDecimal("5000")));
    }

    @Test
    void shouldBeEqualWhenAllFieldsMatch() {
        assertEquals(fullRequestBuilder().build(), fullRequestBuilder().build());
    }

    @Test
    void shouldBeEqualWhenAllOptionalFieldsAreNull() {
        SimulationRequest r1 = SimulationRequest.getBuilder().build();
        SimulationRequest r2 = SimulationRequest.getBuilder().build();

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
            .equals(fullRequestBuilder().amount(Money.of(new BigDecimal("600000"))).build()));
    }

    @Test
    void shouldNotBeEqualWhenDurationDiffers() {
        assertFalse(fullRequestBuilder().build().equals(fullRequestBuilder().duration(120).build()));
    }

    @Test
    void shouldNotBeEqualWhenNationalityCodeDiffers() {
        assertFalse(fullRequestBuilder().build().equals(fullRequestBuilder().nationalityCode("FR").build()));
    }

    @Test
    void shouldNotBeEqualWhenMoroccanResidentDiffers() {
        assertFalse(fullRequestBuilder().build().equals(fullRequestBuilder().moroccanResident(false).build()));
    }

    @Test
    void shouldNotBeEqualWhenPropertyValueDiffers() {
        assertFalse(fullRequestBuilder().build()
            .equals(fullRequestBuilder().propertyValue(Money.of(new BigDecimal("700000"))).build()));
    }

    @Test
    void shouldNotBeEqualWhenMonthlyIncomeDiffers() {
        assertFalse(fullRequestBuilder().build()
            .equals(fullRequestBuilder().monthlyIncome(Money.of(new BigDecimal("25000"))).build()));
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
            .equals(fullRequestBuilder().coBorrowerMonthlyIncome(Money.of(new BigDecimal("9000"))).build()));
    }

    @Test
    void shouldTreatNullCoBorrowerAsFalse() {
        SimulationRequest req = SimulationRequest.getBuilder().coBorrower(null).build();
        assertFalse(req.getCoBorrower());
    }

    @Test
    void shouldTreatExplicitFalseCoBorrowerAsFalse() {
        SimulationRequest req = SimulationRequest.getBuilder().coBorrower(false).build();
        assertFalse(req.getCoBorrower());
    }

    @Test
    void shouldTreatExplicitTrueCoBorrowerAsTrue() {
        SimulationRequest req = SimulationRequest.getBuilder().coBorrower(true).build();
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
            Money.of(new BigDecimal("2800")), zero, Money.of(new BigDecimal("2800")),
            Money.of(new BigDecimal("72000")), zero, Money.of(new BigDecimal("572000")),
            Money.of(new BigDecimal("7500")), Money.of(new BigDecimal("7500")),
            Money.of(new BigDecimal("5000")), Money.of(new BigDecimal("5000")), Money.of(new BigDecimal("25000")));
        FinancialSnapshot max = new FinancialSnapshot(
            Money.of(new BigDecimal("3200")), zero, Money.of(new BigDecimal("3200")),
            Money.of(new BigDecimal("168000")), zero, Money.of(new BigDecimal("668000")),
            Money.of(new BigDecimal("7500")), Money.of(new BigDecimal("7500")),
            Money.of(new BigDecimal("5000")), Money.of(new BigDecimal("5000")), Money.of(new BigDecimal("25000")));

        SimulationResponse resp = SimulationResponse.getBuilder()
            .amount(Money.of(new BigDecimal("500000")))
            .duration(240)
            .cspCode(CspCode.SALA)
            .nationalityCode("MA")
            .moroccanResident(true)
            .applicableRate(Rate.of(new BigDecimal("4.80")))
            .rateMin(Rate.of(new BigDecimal("3.50")))
            .rateMax(Rate.of(new BigDecimal("5.50")))
            .applicable(min)
            .bounds(new RateBoundsSnapshot(min, max))
            .build();

        assertNotNull(resp);
        assertEquals(0, resp.getAmount().amount().compareTo(new BigDecimal("500000")));
        assertEquals(240, resp.getDuration());
        assertEquals(CspCode.SALA, resp.getCspCode());
        assertEquals("MA", resp.getNationalityCode());
        assertTrue(resp.getMoroccanResident());
        assertEquals(0, resp.getApplicableRate().value().compareTo(new BigDecimal("4.80")));
        assertEquals(0, resp.getBounds().min().registrationFees().amount().compareTo(new BigDecimal("7500")));
    }

    @Test
    void shouldSimulationResponseEqualsAndHashCode() {
        SimulationResponse r1 = SimulationResponse.getBuilder().amount(Money.of(new BigDecimal("500000"))).build();
        SimulationResponse r2 = SimulationResponse.getBuilder().amount(Money.of(new BigDecimal("500000"))).build();
        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void shouldSimulationResponseToString() {
        SimulationResponse resp = SimulationResponse.getBuilder()
            .amount(Money.of(new BigDecimal("500000")))
            .cspCode(CspCode.SALA)
            .build();
        String str = resp.toString();
        assertTrue(str.contains("500000"));
        assertTrue(str.contains("SALA"));
    }
}