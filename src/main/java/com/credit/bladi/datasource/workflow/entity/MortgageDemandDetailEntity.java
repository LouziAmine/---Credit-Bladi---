package com.credit.bladi.datasource.workflow.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "mortgage_demand_detail")
public class MortgageDemandDetailEntity {
    @Id
    @Column(name = "demand_id")
    private Long demandId;

    @Column(name = "product_type", nullable = false, length = 20)
    private String productType;

    @Column(name = "csp_code", length = 10)
    private String cspCode;

    @Column(name = "nationality_code", length = 10)
    private String nationalityCode;

    @Column(name = "moroccan_resident")
    private Boolean moroccanResident;

    @Column(name = "property_value", precision = 15, scale = 2)
    private BigDecimal propertyValue;

    @Column(name = "monthly_income", precision = 15, scale = 2)
    private BigDecimal monthlyIncome;

    @Column(name = "age")
    private Integer age;

    @Column(name = "co_borrower")
    private Boolean coBorrower;

    @Column(name = "co_borrower_monthly_income", precision = 15, scale = 2)
    private BigDecimal coBorrowerMonthlyIncome;

    public MortgageDemandDetailEntity() {
    }

    public Long getDemandId() {
        return demandId;
    }

    public void setDemandId(Long demandId) {
        this.demandId = demandId;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getCspCode() {
        return cspCode;
    }

    public void setCspCode(String cspCode) {
        this.cspCode = cspCode;
    }

    public String getNationalityCode() {
        return nationalityCode;
    }

    public void setNationalityCode(String nationalityCode) {
        this.nationalityCode = nationalityCode;
    }

    public Boolean getMoroccanResident() {
        return moroccanResident;
    }

    public void setMoroccanResident(Boolean moroccanResident) {
        this.moroccanResident = moroccanResident;
    }

    public BigDecimal getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(BigDecimal propertyValue) {
        this.propertyValue = propertyValue;
    }

    public BigDecimal getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(BigDecimal monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Boolean getCoBorrower() {
        return coBorrower;
    }

    public void setCoBorrower(Boolean coBorrower) {
        this.coBorrower = coBorrower;
    }

    public BigDecimal getCoBorrowerMonthlyIncome() {
        return coBorrowerMonthlyIncome;
    }

    public void setCoBorrowerMonthlyIncome(BigDecimal coBorrowerMonthlyIncome) {
        this.coBorrowerMonthlyIncome = coBorrowerMonthlyIncome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MortgageDemandDetailEntity other)) {
            return false;
        }
        return demandId != null && demandId.equals(other.demandId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
