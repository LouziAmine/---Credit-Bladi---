package com.credit.bladi.unittest.datasource.enterprise.mapper;

import static com.credit.bladi.unittest.mock.data.MockDates.DATE_2026_01_01T00_00_00Z;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_RC_45210;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_JDUPONT;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_MANAGER1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.credit.bladi.datasource.enterprise.entity.EnterpriseUserLinkEntity;
import com.credit.bladi.datasource.enterprise.mapper.EnterpriseUserLinkEntityMapper;
import com.credit.bladi.model.enterprise.EnterpriseUserLinkModel;
import com.credit.bladi.model.enums.EnterpriseLinkOrigin;
import com.credit.bladi.model.enums.EnterpriseLinkRole;
import com.credit.bladi.model.enums.EnterpriseLinkStatus;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class EnterpriseUserLinkEntityMapperTest {
    @Test
    void shouldMapNewEntityWithGivenFields() {
        EnterpriseUserLinkEntity entity = EnterpriseUserLinkEntityMapper.mapToNewEntity(1L, 5L,
            EnterpriseLinkRole.RESPONSABLE, EnterpriseLinkStatus.PENDING, EnterpriseLinkOrigin.CLIENT_CLAIM,
            LABEL_RC_45210, USER_JDUPONT, Instant.parse(DATE_2026_01_01T00_00_00Z));

        assertEquals(1L, entity.getEnterpriseId());
        assertEquals(5L, entity.getUserId());
        assertEquals(EnterpriseLinkRole.RESPONSABLE, entity.getRole());
        assertEquals(EnterpriseLinkStatus.PENDING, entity.getStatus());
        assertEquals(EnterpriseLinkOrigin.CLIENT_CLAIM, entity.getOrigin());
        assertEquals(LABEL_RC_45210, entity.getClaimedRegistrationNumber());
        assertEquals(USER_JDUPONT, entity.getRequestedByUsername());
        assertNull(entity.getDecidedByUsername());
        assertNull(entity.getDecidedAt());
        assertEquals(Instant.parse(DATE_2026_01_01T00_00_00Z), entity.getCreatedAt());
    }

    @Test
    void shouldMapFromEntityToModel() {
        EnterpriseUserLinkEntity entity = new EnterpriseUserLinkEntity();
        entity.setId(10L);
        entity.setEnterpriseId(1L);
        entity.setUserId(5L);
        entity.setRole(EnterpriseLinkRole.DELEGUE);
        entity.setStatus(EnterpriseLinkStatus.APPROVED);
        entity.setOrigin(EnterpriseLinkOrigin.MANAGER_INVITE);
        entity.setRequestedByUsername(USER_MANAGER1);
        Instant now = Instant.now();
        entity.setCreatedAt(now);

        UUID enterprisePublicId = UUID.randomUUID();
        UUID userPublicId = UUID.randomUUID();
        EnterpriseUserLinkModel model = EnterpriseUserLinkEntityMapper.mapFromEntity(entity, enterprisePublicId,
            userPublicId, USER_JDUPONT);

        assertEquals(10L, model.id());
        assertEquals(1L, model.enterpriseId());
        assertEquals(enterprisePublicId, model.enterprisePublicId());
        assertEquals(5L, model.userId());
        assertEquals(userPublicId, model.userPublicId());
        assertEquals(USER_JDUPONT, model.username());
        assertEquals(EnterpriseLinkRole.DELEGUE, model.role());
        assertEquals(EnterpriseLinkStatus.APPROVED, model.status());
        assertEquals(EnterpriseLinkOrigin.MANAGER_INVITE, model.origin());
        assertEquals(USER_MANAGER1, model.requestedByUsername());
        assertNull(model.decidedByUsername());
        assertNull(model.decidedAt());
        assertEquals(now, model.createdAt());
    }
}
