package com.credit.bladi.unittest.repository.workflow.detail;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.datasource.workflow.EnterpriseDemandDetailDatasource;
import com.credit.bladi.datasource.workflow.entity.EnterpriseDemandDetailEntity;
import com.credit.bladi.repository.workflow.detail.EnterpriseDemandDetailStore;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EnterpriseDemandDetailStoreTest {
    @Mock
    private EnterpriseDemandDetailDatasource datasource;

    private EnterpriseDemandDetailStore store;

    @BeforeEach
    void setUp() {
        store = new EnterpriseDemandDetailStore(datasource);
    }

    @Test
    void shouldAttachEnterpriseToExistingDetailRow() {
        EnterpriseDemandDetailEntity existing = new EnterpriseDemandDetailEntity();
        existing.setDemandId(1L);
        when(datasource.findById(1L)).thenReturn(Optional.of(existing));

        store.attachEnterprise(1L, 42L);

        ArgumentCaptor<EnterpriseDemandDetailEntity> captor =
            ArgumentCaptor.forClass(EnterpriseDemandDetailEntity.class);
        verify(datasource).save(captor.capture());
        assertEquals(42L, captor.getValue().getEnterpriseId());
    }
}
