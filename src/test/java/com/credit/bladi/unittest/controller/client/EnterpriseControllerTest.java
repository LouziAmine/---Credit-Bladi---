package com.credit.bladi.unittest.controller.client;

import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_ATLAS_TEXTILE;
import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_ATLAS_TEXTILE_SARL;
import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_UNKNOWN_CO;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_RC_45210;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_TEXTILE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.controller.client.EnterpriseController;
import com.credit.bladi.controller.dto.client.request.CreateEnterpriseRequestDTO;
import com.credit.bladi.controller.dto.client.response.EnterpriseMatchResponseDTO;
import com.credit.bladi.controller.dto.client.response.EnterpriseResponseDTO;
import com.credit.bladi.model.client.EnterpriseModel;
import com.credit.bladi.service.client.IEnterpriseService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class EnterpriseControllerTest {
    @Mock
    private IEnterpriseService enterpriseService;

    private EnterpriseController controller;

    @BeforeEach
    void setUp() {
        controller = new EnterpriseController(enterpriseService);
    }

    private static EnterpriseModel enterprise(Long id, UUID publicId) {
        return new EnterpriseModel(id, publicId, COMPANY_ATLAS_TEXTILE_SARL, LABEL_TEXTILE, LABEL_RC_45210, null,
            Instant.now());
    }

    @Test
    void shouldReturn201WithLocationWhenCreatingEnterprise() {
        UUID publicId = UUID.randomUUID();
        EnterpriseModel created = enterprise(1L, publicId);
        when(enterpriseService.createEnterprise(COMPANY_ATLAS_TEXTILE_SARL, LABEL_TEXTILE,
            LABEL_RC_45210)).thenReturn(created);

        ResponseEntity<EnterpriseResponseDTO> response = controller.createEnterprise(
            new CreateEnterpriseRequestDTO(COMPANY_ATLAS_TEXTILE_SARL, LABEL_TEXTILE, LABEL_RC_45210));

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("/api/v1/enterprises/" + publicId, response.getHeaders().getLocation().toString());
        assertEquals(COMPANY_ATLAS_TEXTILE_SARL, response.getBody().name());
    }

    @Test
    void shouldDelegatePagingToServiceWhenListingEnterprises() {
        CreditPage<EnterpriseModel> page = new CreditPage<>();
        page.setItems(List.of(enterprise(1L, UUID.randomUUID())));
        page.setCurrentPage(2);
        page.setSize(10);
        page.setTotal(1);
        when(enterpriseService.getAllEnterprises(2, 10)).thenReturn(page);

        ResponseEntity<CreditPage<EnterpriseResponseDTO>> response = controller.getAllEnterprises(2, 10);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().getItems().size());
        assertEquals(COMPANY_ATLAS_TEXTILE_SARL, response.getBody().getItems().get(0).name());
    }

    @Test
    void shouldResolvePublicIdThenReturnEnterpriseById() {
        UUID publicId = UUID.randomUUID();
        when(enterpriseService.resolveEnterpriseId(publicId)).thenReturn(1L);
        when(enterpriseService.getEnterpriseById(1L)).thenReturn(enterprise(1L, publicId));

        ResponseEntity<EnterpriseResponseDTO> response = controller.getEnterpriseById(publicId);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(publicId, response.getBody().id());
    }

    @Test
    void shouldReturnMatchedTrueWhenEnterpriseFoundByName() {
        UUID publicId = UUID.randomUUID();
        when(enterpriseService.matchEnterpriseByName(COMPANY_ATLAS_TEXTILE))
            .thenReturn(Optional.of(enterprise(1L, publicId)));

        ResponseEntity<EnterpriseMatchResponseDTO> response = controller.matchEnterpriseByName(COMPANY_ATLAS_TEXTILE);

        assertTrue(response.getBody().matched());
        assertEquals(COMPANY_ATLAS_TEXTILE_SARL, response.getBody().enterprise().name());
    }

    @Test
    void shouldReturnMatchedFalseWhenNoEnterpriseFoundByName() {
        when(enterpriseService.matchEnterpriseByName(COMPANY_UNKNOWN_CO)).thenReturn(Optional.empty());

        ResponseEntity<EnterpriseMatchResponseDTO> response = controller.matchEnterpriseByName(COMPANY_UNKNOWN_CO);

        assertFalse(response.getBody().matched());
        assertNull(response.getBody().enterprise());
    }
}
