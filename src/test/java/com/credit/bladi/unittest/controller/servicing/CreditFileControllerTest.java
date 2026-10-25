package com.credit.bladi.unittest.controller.servicing;

import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_15000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1500000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_500000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_600000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_22186_45;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_LATE;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_PAID;
import static com.credit.bladi.unittest.mock.data.MockCodes.REFERENCE_DEM_3F2A9C1B;
import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_ATLAS_TEXTILE_SARL;
import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_CREDIT_BLADI_SA;
import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_C_AT_X_MA;
import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_K_AT_X_MA;
import static com.credit.bladi.unittest.mock.data.MockContacts.PERSON_KARIM_BENALI;
import static com.credit.bladi.unittest.mock.data.MockContacts.PHONE_0600000000;
import static com.credit.bladi.unittest.mock.data.MockContacts.PHONE_0600000001;
import static com.credit.bladi.unittest.mock.data.MockContacts.RIB_230780000000000000000136;
import static com.credit.bladi.unittest.mock.data.MockContacts.RIB_230_780_0000000000000000_39;
import static com.credit.bladi.unittest.mock.data.MockContacts.RIB_230_780_0123456789012345_03;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_DEM_MTG1;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_DOS_3F2A9C1B;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_TEXTILE;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_CLIENT1;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_MANAGER1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.controller.dto.servicing.request.RejectPaymentRequestDTO;
import com.credit.bladi.controller.dto.servicing.response.CreditFileResponseDTO;
import com.credit.bladi.controller.dto.servicing.response.InstallmentResponseDTO;
import com.credit.bladi.controller.servicing.CreditFileController;
import com.credit.bladi.model.enums.CreditFileStatus;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.model.enums.InstallmentStatus;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.model.servicing.CreditFileModel;
import com.credit.bladi.model.servicing.InstallmentModel;
import com.credit.bladi.model.servicing.PaymentBeneficiary;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rib;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.service.client.IEnterpriseService;
import com.credit.bladi.service.servicing.ICreditFileService;
import com.credit.bladi.service.workflow.engine.IWorkflowEngineService;
import com.credit.bladi.unittest.mock.workflow.MockDemand;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class CreditFileControllerTest {
    private static final String BENEFICIARY_NAME = COMPANY_CREDIT_BLADI_SA;

    private static final String BENEFICIARY_RIB = RIB_230_780_0000000000000000_39;

    @Mock
    private ICreditFileService creditFileService;

    @Mock
    private IWorkflowEngineService workflowEngineService;

    @Mock
    private IEnterpriseService enterpriseService;

    private CreditFileController controller;

    @BeforeEach
    void setUp() {
        controller = new CreditFileController(creditFileService, workflowEngineService, enterpriseService,
            new PaymentBeneficiary(BENEFICIARY_NAME, Rib.of(BENEFICIARY_RIB)));
    }

    private static CreditFileModel creditFile() {
        return new CreditFileModel(1L, UUID.randomUUID(), 1L, LABEL_DOS_3F2A9C1B, Instant.now(),
            Money.of(AMOUNT_1500000), CreditFileStatus.ACTIVE, USER_MANAGER1);
    }

    private static InstallmentModel installment() {
        return new InstallmentModel(1L, UUID.randomUUID(), 1L, 1, LocalDate.now().plusDays(30),
            Money.of(DECIMAL_22186_45), InstallmentStatus.PENDING, null, null, null, null, null, null);
    }

    @Test
    void shouldReturnCreditFileWithClientRibWhenDemandHasEnterprise() {
        UUID publicDemandId = UUID.randomUUID();
        when(workflowEngineService.resolveDemandId(publicDemandId)).thenReturn(1L);
        CreditFileModel creditFile = creditFile();
        when(creditFileService.getByDemandId(1L)).thenReturn(creditFile);
        DemandModel demand = MockDemand.enterprise(1L, publicDemandId, REFERENCE_DEM_3F2A9C1B, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, Money.of(AMOUNT_1500000), 84, Instant.now(), Instant.now(), null,
            EMAIL_C_AT_X_MA, PHONE_0600000000, null, null, null, null, 7L, null, LABEL_TEXTILE, 5);
        when(workflowEngineService.getDemand(1L)).thenReturn(demand);
        when(enterpriseService.getRibOrNull(7L)).thenReturn(RIB_230_780_0123456789012345_03);
        when(creditFileService.getInstallments(1L)).thenReturn(List.of(installment()));

        ResponseEntity<CreditFileResponseDTO> response = controller.getByDemandId(publicDemandId);

        assertEquals(200, response.getStatusCode().value());
        CreditFileResponseDTO body = response.getBody();
        assertEquals(LABEL_DOS_3F2A9C1B, body.reference());
        assertEquals(BENEFICIARY_NAME, body.beneficiaryName());
        assertEquals(Rib.of(BENEFICIARY_RIB).value(), body.beneficiaryRib());
        assertEquals(RIB_230_780_0123456789012345_03, body.clientRib());
        assertEquals(1, body.installments().size());
    }

    @Test
    void shouldReturnNullClientRibWhenDemandHasNoEnterprise() {
        UUID publicDemandId = UUID.randomUUID();
        when(workflowEngineService.resolveDemandId(publicDemandId)).thenReturn(2L);
        when(creditFileService.getByDemandId(2L)).thenReturn(creditFile());
        DemandModel demand = MockDemand.mortgage(2L, publicDemandId, LABEL_DEM_MTG1, 1L, PERSON_KARIM_BENALI,
            Money.of(AMOUNT_500000), 240, Instant.now(), Instant.now(), null, EMAIL_K_AT_X_MA, PHONE_0600000001,
            null, ProductType.CONVENTIONAL, CspCode.SALA, Money.of(AMOUNT_600000),
            Money.of(AMOUNT_15000), 35, false, null);
        when(workflowEngineService.getDemand(2L)).thenReturn(demand);
        when(creditFileService.getInstallments(1L)).thenReturn(List.of());

        ResponseEntity<CreditFileResponseDTO> response = controller.getByDemandId(publicDemandId);

        assertNull(response.getBody().clientRib());
        verify(enterpriseService).getRibOrNull(null);
    }

    @Test
    void shouldValidatePaymentAndNotifyClient() {
        UUID publicDemandId = UUID.randomUUID();
        UUID publicInstallmentId = UUID.randomUUID();
        when(workflowEngineService.resolveDemandId(publicDemandId)).thenReturn(1L);
        when(creditFileService.resolveInstallmentId(publicInstallmentId)).thenReturn(9L);
        InstallmentModel paid = new InstallmentModel(9L, publicInstallmentId, 1L, 1, LocalDate.now(),
            Money.of(DECIMAL_22186_45), InstallmentStatus.PAID, Instant.now(), null, Instant.now(),
            USER_CLIENT1, RIB_230780000000000000000136, null);
        when(creditFileService.validatePayment(1L, 9L)).thenReturn(paid);

        ResponseEntity<InstallmentResponseDTO> response = controller.validatePayment(publicDemandId,
            publicInstallmentId);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(CODE_PAID, response.getBody().status());
    }

    @Test
    void shouldRejectPaymentWithReasonAndNotifyClient() {
        UUID publicDemandId = UUID.randomUUID();
        UUID publicInstallmentId = UUID.randomUUID();
        when(workflowEngineService.resolveDemandId(publicDemandId)).thenReturn(1L);
        when(creditFileService.resolveInstallmentId(publicInstallmentId)).thenReturn(9L);
        String reason = "Le RIB déclaré ne correspond à aucun versement reçu.";
        InstallmentModel reverted = new InstallmentModel(9L, publicInstallmentId, 1L, 1, LocalDate.now().minusDays(2),
            Money.of(DECIMAL_22186_45), InstallmentStatus.LATE, null, null, null, null, null, null);
        when(creditFileService.rejectPayment(1L, 9L, reason)).thenReturn(reverted);

        ResponseEntity<InstallmentResponseDTO> response = controller.rejectPayment(publicDemandId,
            publicInstallmentId, new RejectPaymentRequestDTO(reason));

        assertEquals(200, response.getStatusCode().value());
        assertEquals(CODE_LATE, response.getBody().status());
    }
}
