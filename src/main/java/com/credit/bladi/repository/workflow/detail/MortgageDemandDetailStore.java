package com.credit.bladi.repository.workflow.detail;

import static com.credit.bladi.exception.WorkflowException.WorkflowExceptionType.DEMAND_NOT_FOUND;

import com.credit.bladi.datasource.workflow.MortgageDemandDetailDatasource;
import com.credit.bladi.datasource.workflow.entity.MortgageDemandDetailEntity;
import com.credit.bladi.exception.WorkflowException;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.DemandType;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.model.vo.ValueObjectMappers;
import com.credit.bladi.model.workflow.DemandDetail;
import com.credit.bladi.model.workflow.DemandSnapshotDetail;
import com.credit.bladi.model.workflow.MortgageDemandDetail;
import com.credit.bladi.model.workflow.MortgageDemandSnapshotDetail;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class MortgageDemandDetailStore implements DemandDetailStore {
    private final MortgageDemandDetailDatasource datasource;

    public MortgageDemandDetailStore(MortgageDemandDetailDatasource datasource) {
        this.datasource = datasource;
    }

    @Override
    public DemandType supportedType() {
        return DemandType.MORTGAGE;
    }

    @Override
    public void save(Long demandId, DemandSnapshotDetail snapshotDetail) {
        MortgageDemandSnapshotDetail mortgage = (MortgageDemandSnapshotDetail) snapshotDetail;
        MortgageDemandDetailEntity detail = new MortgageDemandDetailEntity();
        detail.setDemandId(demandId);
        detail.setProductType(mortgage.productType().name());
        detail.setCspCode(mortgage.cspCode() != null ? mortgage.cspCode().name() : null);
        detail.setNationalityCode(mortgage.nationalityCode());
        detail.setMoroccanResident(mortgage.moroccanResident());
        detail.setPropertyValue(ValueObjectMappers.toBigDecimal(mortgage.propertyValue()));
        detail.setMonthlyIncome(ValueObjectMappers.toBigDecimal(mortgage.monthlyIncome()));
        detail.setAge(mortgage.age());
        detail.setCoBorrower(mortgage.coBorrower());
        detail.setCoBorrowerMonthlyIncome(ValueObjectMappers.toBigDecimal(mortgage.coBorrowerMonthlyIncome()));
        datasource.save(detail);
    }

    @Override
    public DemandDetail load(Long demandId) {
        return toDetail(datasource.findById(demandId).orElseThrow(() -> WorkflowException.of(DEMAND_NOT_FOUND)));
    }

    @Override
    public Map<Long, DemandDetail> loadAll(List<Long> demandIds) {
        return datasource.findAllById(demandIds).stream()
            .collect(Collectors.toMap(MortgageDemandDetailEntity::getDemandId, this::toDetail));
    }

    private DemandDetail toDetail(MortgageDemandDetailEntity e) {
        return new MortgageDemandDetail(ProductType.valueOf(e.getProductType()),
            e.getCspCode() != null ? CspCode.valueOf(e.getCspCode()) : null, e.getNationalityCode(),
            e.getMoroccanResident(), ValueObjectMappers.toMoney(e.getPropertyValue()),
            ValueObjectMappers.toMoney(e.getMonthlyIncome()), e.getAge(), e.getCoBorrower(),
            ValueObjectMappers.toMoney(e.getCoBorrowerMonthlyIncome()));
    }
}
