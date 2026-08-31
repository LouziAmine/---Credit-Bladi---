package com.credit.bladi.datasource.workflow.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "workflow_process_step")
public class WorkflowProcessStepEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "public_id", nullable = false, unique = true)
    private UUID publicId;

    @Column(name = "workflow_process_id", nullable = false)
    private Long workflowProcessId;

    @Column(name = "step_id", nullable = false)
    private Long stepId;

    @Column(name = "sequence_order", nullable = false)
    private Integer sequenceOrder;

    public WorkflowProcessStepEntity() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WorkflowProcessStepEntity other)) {
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
            "WorkflowProcessStepEntity [id=%s, workflowProcessId=%s, stepId=%s, sequenceOrder=%s]",
            id, workflowProcessId, stepId, sequenceOrder);
    }
}
