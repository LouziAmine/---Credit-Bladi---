package com.credit.bladi.repository.workflow.detail;

import static com.credit.bladi.exception.WorkflowException.WorkflowExceptionType.DEMAND_NOT_FOUND;

import com.credit.bladi.datasource.workflow.EnterpriseDemandDetailDatasource;
import com.credit.bladi.datasource.workflow.entity.EnterpriseDemandDetailEntity;
import com.credit.bladi.exception.WorkflowException;
import com.credit.bladi.model.enums.DemandType;
import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.model.vo.ValueObjectMappers;
import com.credit.bladi.model.workflow.DemandDetail;
import com.credit.bladi.model.workflow.DemandSnapshotDetail;
import com.credit.bladi.model.workflow.EnterpriseDemandDetail;
import com.credit.bladi.model.workflow.EnterpriseDemandSnapshotDetail;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class EnterpriseDemandDetailStore implements DemandDetailStore {
    private final EnterpriseDemandDetailDatasource datasource;

    public EnterpriseDemandDetailStore(EnterpriseDemandDetailDatasource datasource) {
        this.datasource = datasource;
    }

    @Override
    public DemandType supportedType() {
        return DemandType.ENTERPRISE;
    }

    @Override
    public void save(Long demandId, DemandSnapshotDetail snapshotDetail) {
        EnterpriseDemandSnapshotDetail enterprise = (EnterpriseDemandSnapshotDetail) snapshotDetail;
        EnterpriseDemandDetailEntity detail = new EnterpriseDemandDetailEntity();
        detail.setDemandId(demandId);
        detail.setEnterpriseType(enterprise.enterpriseType().name());
        detail.setAnnualRevenue(ValueObjectMappers.toBigDecimal(enterprise.annualRevenue()));
        detail.setExistingAnnualDebtService(ValueObjectMappers.toBigDecimal(enterprise.existingAnnualDebtService()));
        detail.setNetAnnualResult(ValueObjectMappers.toBigDecimal(enterprise.netAnnualResult()));
        detail.setGuaranteesValue(ValueObjectMappers.toBigDecimal(enterprise.guaranteesValue()));
        detail.setSector(enterprise.sector());
        detail.setCompanyAgeYears(enterprise.companyAgeYears());
        datasource.save(detail);
    }

    @Override
    public void attachEnterprise(Long demandId, Long enterpriseId) {
        EnterpriseDemandDetailEntity detail = datasource.findById(demandId)
            .orElseThrow(() -> WorkflowException.of(DEMAND_NOT_FOUND));
        detail.setEnterpriseId(enterpriseId);
        datasource.save(detail);
    }

    @Override
    public DemandDetail load(Long demandId) {
        return toDetail(datasource.findById(demandId).orElseThrow(() -> WorkflowException.of(DEMAND_NOT_FOUND)));
    }

    @Override
    public Map<Long, DemandDetail> loadAll(List<Long> demandIds) {
        return datasource.findAllById(demandIds).stream()
            .collect(Collectors.toMap(EnterpriseDemandDetailEntity::getDemandId, this::toDetail));
    }

    private DemandDetail toDetail(EnterpriseDemandDetailEntity e) {
        UUID enterprisePublicId = e.getEnterpriseId() == null ? null
            : datasource.findEnterprisePublicId(e.getEnterpriseId()).orElse(null);
        return new EnterpriseDemandDetail(EnterpriseType.valueOf(e.getEnterpriseType()),
            ValueObjectMappers.toMoney(e.getAnnualRevenue()),
            ValueObjectMappers.toMoney(e.getExistingAnnualDebtService()),
            ValueObjectMappers.toMoney(e.getNetAnnualResult()),
            ValueObjectMappers.toMoney(e.getGuaranteesValue()),
            e.getEnterpriseId(), enterprisePublicId, e.getSector(), e.getCompanyAgeYears());
    }
}
