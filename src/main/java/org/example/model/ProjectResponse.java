package org.example.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectResponse {
    private int projectId;
    private String projectName;
    private String formattedProjectStart; // Keep this as String for formatted date
    private String formattedProjectFinish; // Keep this as String for formatted date
    private boolean projectIsActive;
    private List<TaskResponse> tasks;

    public ProjectResponse(Project project) {
        this.projectId = project.getProjectId();
        this.projectName = project.getProjectName();
        this.formattedProjectStart = getFormattedDate(project.getProjectStart()); // Format the project start date
        this.formattedProjectFinish = getFormattedDate(project.getProjectFinish()); // Format the project finish date
        this.projectIsActive = project.getProjectIsActive();
        this.tasks = project.getTasks().stream()
                .map(TaskResponse::new)
                .collect(Collectors.toList());
    }

    // Getters
    public int getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getFormattedProjectStart() {
        return formattedProjectStart; // Return formatted start date
    }

    public String getFormattedProjectFinish() {
        return formattedProjectFinish; // Return formatted finish date
    }

    public boolean isProjectIsActive() {
        return projectIsActive;
    }

    public List<TaskResponse> getTasks() {
        return tasks;
    }

    private String getFormattedDate(Timestamp timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(timestamp);
    }
}

