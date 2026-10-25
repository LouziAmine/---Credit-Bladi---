package com.credit.bladi.unittest.repository.workflow.detail;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.credit.bladi.datasource.workflow.MortgageDemandDetailDatasource;
import com.credit.bladi.repository.workflow.detail.MortgageDemandDetailStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MortgageDemandDetailStoreTest {
    @Mock
    private MortgageDemandDetailDatasource datasource;

    private MortgageDemandDetailStore store;

    @BeforeEach
    void setUp() {
        store = new MortgageDemandDetailStore(datasource);
    }

    @Test
    void shouldRejectAttachingAnEnterpriseToAMortgageDemand() {
        assertThrows(UnsupportedOperationException.class, () -> store.attachEnterprise(1L, 42L));
    }
}
