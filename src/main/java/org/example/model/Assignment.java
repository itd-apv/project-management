package org.example.model;

import java.math.BigDecimal;

public class Assignment {
    private int id;
    private int taskId;
    private int resourceId;
    private BigDecimal etc;
    private BigDecimal actuals;

    public Assignment(int id, int taskId, int resourceId, BigDecimal etc, BigDecimal actuals) {
        this.id = id;
        this.taskId = taskId;
        this.resourceId = resourceId;
        this.etc = etc;
        this.actuals = actuals;
    }

    // Getters and Setters
    public int getAssignmentId() { return id; }
    public void setAssignmentId(int id) {this.id = id; }

    public int getTaskId() {
        return taskId;
    }
    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getResourceId() {
        return resourceId;
    }
    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public BigDecimal getEtc() {
        return etc;
    }
    public void setEtc(BigDecimal etc) {
        this.etc = etc;
    }

    public BigDecimal getActuals() {
        return actuals;
    }
    public void setActuals(BigDecimal actuals) {
        this.actuals = actuals;
    }

    @Override
    public String toString() {
        return "Assignment{" +
                "assignmentId=" + id +
                ", taskId=" + taskId +
                ", resourceId=" + resourceId +
                ", etc=" + etc +
                ", actuals=" + actuals +
                '}';
    }
}