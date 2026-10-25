package com.credit.bladi.datasource.workflow.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "workflow_process_step_demand")
public class WorkflowProcessStepDemandEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "public_id", nullable = false, unique = true)
    private UUID publicId;

    @Column(name = "demand_id", nullable = false)
    private Long demandId;

    @Column(name = "workflow_process_step_id", nullable = false)
    private Long workflowProcessStepId;

    @Column(name = "assigned_username", length = 100)
    private String assignedUsername;

    @Column(name = "assigned_by", length = 100)
    private String assignedBy;

    @Column(name = "entered_at", nullable = false)
    private Instant enteredAt;

    @Column(name = "exited_at")
    private Instant exitedAt;

    @Column(name = "decision_note", length = 500)
    private String decisionNote;

    @Column(name = "decision_outcome", length = 20)
    private String decisionOutcome;

    @Column(name = "classification_outcome", length = 20)
    private String classificationOutcome;

    @Column(name = "return_reason_code", length = 100)
    private String returnReasonCode;

    @Column(name = "recommendations", length = 500)
    private String recommendations;

    @Column(name = "reserves", length = 500)
    private String reserves;

    @Column(name = "committee_type", length = 20)
    private String committeeType;

    @Column(name = "insurance_adi_subscribed")
    private Boolean insuranceAdiSubscribed;

    @Column(name = "insurance_coverage_type", length = 30)
    private String insuranceCoverageType;

    @Column(name = "insurance_damage_subscribed")
    private Boolean insuranceDamageSubscribed;

    @Column(name = "rate_option", length = 20)
    private String rateOption;

    @Column(name = "previous_rate", precision = 5, scale = 2)
    private BigDecimal previousRate;

    @Column(name = "applied_rate", precision = 5, scale = 2)
    private BigDecimal appliedRate;

    @Column(name = "rate_changed_by", length = 100)
    private String rateChangedBy;

    @Column(name = "amount_option", length = 20)
    private String amountOption;

    @Column(name = "previous_amount", precision = 15, scale = 2)
    private BigDecimal previousAmount;

    @Column(name = "applied_amount", precision = 15, scale = 2)
    private BigDecimal appliedAmount;

    @Column(name = "duration_option", length = 20)
    private String durationOption;

    @Column(name = "previous_duration")
    private Integer previousDuration;

    @Column(name = "applied_duration")
    private Integer appliedDuration;

    @Column(name = "closed_by", length = 100)
    private String closedBy;

    public WorkflowProcessStepDemandEntity() {
    }

    @PrePersist
    private void generatePublicId() {
        if (publicId == null) {
            publicId = UUID.randomUUID();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getPublicId() {
        return publicId;
    }

    public Long getDemandId() {
        return demandId;
    }

    public void setDemandId(Long demandId) {
        this.demandId = demandId;
    }

    public Long getWorkflowProcessStepId() {
        return workflowProcessStepId;
    }

    public void setWorkflowProcessStepId(Long workflowProcessStepId) {
        this.workflowProcessStepId = workflowProcessStepId;
    }

    public String getAssignedUsername() {
        return assignedUsername;
    }

    public void setAssignedUsername(String assignedUsername) {
        this.assignedUsername = assignedUsername;
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }

    public Instant getEnteredAt() {
        return enteredAt;
    }

    public void setEnteredAt(Instant enteredAt) {
        this.enteredAt = enteredAt;
    }

    public Instant getExitedAt() {
        return exitedAt;
    }

    public void setExitedAt(Instant exitedAt) {
        this.exitedAt = exitedAt;
    }

    public String getDecisionNote() {
        return decisionNote;
    }

    public void setDecisionNote(String decisionNote) {
        this.decisionNote = decisionNote;
    }

    public String getDecisionOutcome() {
        return decisionOutcome;
    }

    public void setDecisionOutcome(String decisionOutcome) {
        this.decisionOutcome = decisionOutcome;
    }

    public String getClassificationOutcome() {
        return classificationOutcome;
    }

    public void setClassificationOutcome(String classificationOutcome) {
        this.classificationOutcome = classificationOutcome;
    }

    public String getReturnReasonCode() {
        return returnReasonCode;
    }

    public void setReturnReasonCode(String returnReasonCode) {
        this.returnReasonCode = returnReasonCode;
    }

    public String getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }

    public String getReserves() {
        return reserves;
    }

    public void setReserves(String reserves) {
        this.reserves = reserves;
    }

    public String getCommitteeType() {
        return committeeType;
    }

    public void setCommitteeType(String committeeType) {
        this.committeeType = committeeType;
    }

    public Boolean getInsuranceAdiSubscribed() {
        return insuranceAdiSubscribed;
    }

    public void setInsuranceAdiSubscribed(Boolean insuranceAdiSubscribed) {
        this.insuranceAdiSubscribed = insuranceAdiSubscribed;
    }

    public String getInsuranceCoverageType() {
        return insuranceCoverageType;
    }

    public void setInsuranceCoverageType(String insuranceCoverageType) {
        this.insuranceCoverageType = insuranceCoverageType;
    }

    public Boolean getInsuranceDamageSubscribed() {
        return insuranceDamageSubscribed;
    }

    public void setInsuranceDamageSubscribed(Boolean insuranceDamageSubscribed) {
        this.insuranceDamageSubscribed = insuranceDamageSubscribed;
    }

    public String getRateOption() {
        return rateOption;
    }

    public void setRateOption(String rateOption) {
        this.rateOption = rateOption;
    }

    public BigDecimal getPreviousRate() {
        return previousRate;
    }

    public void setPreviousRate(BigDecimal previousRate) {
        this.previousRate = previousRate;
    }

    public BigDecimal getAppliedRate() {
        return appliedRate;
    }

    public void setAppliedRate(BigDecimal appliedRate) {
        this.appliedRate = appliedRate;
    }

    public String getRateChangedBy() {
        return rateChangedBy;
    }

    public void setRateChangedBy(String rateChangedBy) {
        this.rateChangedBy = rateChangedBy;
    }

    public String getAmountOption() {
        return amountOption;
    }

    public void setAmountOption(String amountOption) {
        this.amountOption = amountOption;
    }

    public BigDecimal getPreviousAmount() {
        return previousAmount;
    }

    public void setPreviousAmount(BigDecimal previousAmount) {
        this.previousAmount = previousAmount;
    }

    public BigDecimal getAppliedAmount() {
        return appliedAmount;
    }

    public void setAppliedAmount(BigDecimal appliedAmount) {
        this.appliedAmount = appliedAmount;
    }

    public String getDurationOption() {
        return durationOption;
    }

    public void setDurationOption(String durationOption) {
        this.durationOption = durationOption;
    }

    public Integer getPreviousDuration() {
        return previousDuration;
    }

    public void setPreviousDuration(Integer previousDuration) {
        this.previousDuration = previousDuration;
    }

    public Integer getAppliedDuration() {
        return appliedDuration;
    }

    public void setAppliedDuration(Integer appliedDuration) {
        this.appliedDuration = appliedDuration;
    }

    public void setClosedBy(String closedBy) {
        this.closedBy = closedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WorkflowProcessStepDemandEntity other)) {
            return false;
        }
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return String.format(
            "WorkflowProcessStepDemandEntity [id=%s, demandId=%s, workflowProcessStepId=%s, "
                + "assignedUsername=%s, enteredAt=%s, exitedAt=%s]",
            id, demandId, workflowProcessStepId, assignedUsername, enteredAt, exitedAt);
    }
}
