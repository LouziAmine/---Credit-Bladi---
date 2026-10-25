package com.credit.bladi.unittest.controller.client;

import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1500000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_22186_45;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_DECLARED;
import static com.credit.bladi.unittest.mock.data.MockCodes.REFERENCE_DEM_3F2A9C1B;
import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_ATLAS_TEXTILE_SARL;
import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_CREDIT_BLADI_SA;
import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_C_AT_X_MA;
import static com.credit.bladi.unittest.mock.data.MockContacts.PHONE_0600000000;
import static com.credit.bladi.unittest.mock.data.MockContacts.RIB_230780000000000000000136;
import static com.credit.bladi.unittest.mock.data.MockContacts.RIB_230_780_0000000000000000_39;
import static com.credit.bladi.unittest.mock.data.MockContacts.RIB_230_780_0123456789012345_03;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_DOS_3F2A9C1B;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_TEXTILE;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_CLIENT1;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_MANAGER1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.controller.client.ClientCreditFileController;
import com.credit.bladi.controller.dto.servicing.request.DeclarePaymentRequestDTO;
import com.credit.bladi.controller.dto.servicing.response.CreditFileResponseDTO;
import com.credit.bladi.controller.dto.servicing.response.InstallmentResponseDTO;
import com.credit.bladi.model.enums.CreditFileStatus;
import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.model.enums.InstallmentStatus;
import com.credit.bladi.model.servicing.CreditFileModel;
import com.credit.bladi.model.servicing.InstallmentModel;
import com.credit.bladi.model.servicing.PaymentBeneficiary;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rib;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.service.client.IEnterpriseService;
import com.credit.bladi.service.security.ClientDemandAccessGuard;
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
class ClientCreditFileControllerTest {
    private static final String BENEFICIARY_NAME = COMPANY_CREDIT_BLADI_SA;

    private static final String BENEFICIARY_RIB = RIB_230_780_0000000000000000_39;

    @Mock
    private ICreditFileService creditFileService;

    @Mock
    private IWorkflowEngineService workflowEngineService;

    @Mock
    private ClientDemandAccessGuard accessGuard;

    @Mock
    private IEnterpriseService enterpriseService;

    private ClientCreditFileController controller;

    @BeforeEach
    void setUp() {
        controller = new ClientCreditFileController(creditFileService, workflowEngineService, accessGuard,
            enterpriseService, new PaymentBeneficiary(BENEFICIARY_NAME, Rib.of(BENEFICIARY_RIB)));
    }

    private static CreditFileModel creditFile() {
        return new CreditFileModel(1L, UUID.randomUUID(), 1L, LABEL_DOS_3F2A9C1B, Instant.now(),
            Money.of(AMOUNT_1500000), CreditFileStatus.ACTIVE, USER_MANAGER1);
    }

    @Test
    void shouldAssertApprovedThenReturnCreditFile() {
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
        when(creditFileService.getInstallments(1L)).thenReturn(List.of());

        ResponseEntity<CreditFileResponseDTO> response = controller.getByDemandId(publicDemandId);

        verify(accessGuard).assertApprovedForDemand(1L);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(LABEL_DOS_3F2A9C1B, response.getBody().reference());
        assertEquals(RIB_230_780_0123456789012345_03, response.getBody().clientRib());
    }

    @Test
    void shouldAssertApprovedThenDeclarePaymentAndNotify() {
        UUID publicDemandId = UUID.randomUUID();
        UUID publicInstallmentId = UUID.randomUUID();
        when(workflowEngineService.resolveDemandId(publicDemandId)).thenReturn(1L);
        when(creditFileService.resolveInstallmentId(publicInstallmentId)).thenReturn(9L);
        InstallmentModel declared = new InstallmentModel(9L, publicInstallmentId, 1L, 1, LocalDate.now(),
            Money.of(DECIMAL_22186_45), InstallmentStatus.DECLARED, null, null, Instant.now(), USER_CLIENT1,
            RIB_230780000000000000000136, null);
        when(creditFileService.declarePayment(1L, 9L, RIB_230780000000000000000136)).thenReturn(declared);

        ResponseEntity<InstallmentResponseDTO> response = controller.payInstallment(publicDemandId,
            publicInstallmentId, new DeclarePaymentRequestDTO(RIB_230780000000000000000136));

        verify(accessGuard).assertApprovedForDemand(1L);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(CODE_DECLARED, response.getBody().status());
        assertEquals(RIB_230780000000000000000136, response.getBody().declaredSourceRib());
    }
}
