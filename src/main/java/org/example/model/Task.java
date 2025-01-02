package org.example.model;

public class Task {
    private int id;
    private String taskName;
    private int projectId;
    private String status;

    // Default constructor
    public Task() {
    }

    // Parameterized constructor
    public Task(int id, String taskName, int projectId, String status) {
        this.id = id;
        this.taskName = taskName;
        this.projectId = projectId;
        this.status = status;
    }

    // Getters and Setters
    public int getTaskId() {
        return id;
    }

    public void setTaskId(int id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getTaskStatus() {
        return status;
    }

    public void setTaskStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskId=" + id +
                ", taskName='" + taskName + '\'' +
                ", projectId=" + projectId +
                ", status='" + status + '\'' +
                '}';
    }
}
