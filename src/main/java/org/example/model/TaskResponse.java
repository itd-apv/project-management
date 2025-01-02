package org.example.model;

class TaskResponse {
    private int taskId;
    private String taskName;
    private int projectId;
    private String taskStatus;

    public TaskResponse(Task task) {
        this.taskId = task.getTaskId();
        this.taskName = task.getTaskName();
        this.projectId = task.getProjectId();
        this.taskStatus = task.getTaskStatus();
    }
    // Getters
    public int getTaskId() {
        return taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public int getProjectId() {
        return projectId;
    }

    public String getTaskStatus() {
        return taskStatus;
    }
}
