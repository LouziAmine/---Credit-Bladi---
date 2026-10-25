package com.credit.bladi.datasource.workflow.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "enterprise_demand_detail")
public class EnterpriseDemandDetailEntity {
    @Id
    @Column(name = "demand_id")
    private Long demandId;

    @Column(name = "enterprise_type", nullable = false, length = 20)
    private String enterpriseType;

    @Column(name = "annual_revenue", precision = 15, scale = 2)
    private BigDecimal annualRevenue;

    @Column(name = "existing_annual_debt_service", precision = 15, scale = 2)
    private BigDecimal existingAnnualDebtService;

    @Column(name = "net_annual_result", precision = 15, scale = 2)
    private BigDecimal netAnnualResult;

    @Column(name = "guarantees_value", precision = 15, scale = 2)
    private BigDecimal guaranteesValue;

    @Column(name = "enterprise_id")
    private Long enterpriseId;

    @Column(name = "sector", length = 100)
    private String sector;

    @Column(name = "company_age_years")
    private Integer companyAgeYears;

    public EnterpriseDemandDetailEntity() {
    }

    public Long getDemandId() {
        return demandId;
    }

    public void setDemandId(Long demandId) {
        this.demandId = demandId;
    }

    public String getEnterpriseType() {
        return enterpriseType;
    }

    public void setEnterpriseType(String enterpriseType) {
        this.enterpriseType = enterpriseType;
    }

    public BigDecimal getAnnualRevenue() {
        return annualRevenue;
    }

    public void setAnnualRevenue(BigDecimal annualRevenue) {
        this.annualRevenue = annualRevenue;
    }

    public BigDecimal getExistingAnnualDebtService() {
        return existingAnnualDebtService;
    }

    public void setExistingAnnualDebtService(BigDecimal existingAnnualDebtService) {
        this.existingAnnualDebtService = existingAnnualDebtService;
    }

    public BigDecimal getNetAnnualResult() {
        return netAnnualResult;
    }

    public void setNetAnnualResult(BigDecimal netAnnualResult) {
        this.netAnnualResult = netAnnualResult;
    }

    public BigDecimal getGuaranteesValue() {
        return guaranteesValue;
    }

    public void setGuaranteesValue(BigDecimal guaranteesValue) {
        this.guaranteesValue = guaranteesValue;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public Integer getCompanyAgeYears() {
        return companyAgeYears;
    }

    public void setCompanyAgeYears(Integer companyAgeYears) {
        this.companyAgeYears = companyAgeYears;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EnterpriseDemandDetailEntity other)) {
            return false;
        }
        return demandId != null && demandId.equals(other.demandId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
