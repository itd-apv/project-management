package org.example.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Project {
    private int id;
    private String name;
    private Timestamp start;
    private Timestamp finish;
    private Timestamp created_date;
    private Boolean is_active;
    private List<Task> tasks; // Change to List<Task>

    // Constructor
    public Project(int id, String name, Timestamp start, Timestamp finish, Timestamp created_date, Boolean is_active) {
        this.id = id;
        this.name = name;
        this.start = start;
        this.finish = finish;
        this.created_date = created_date;
        this.is_active = is_active;
        this.tasks = new ArrayList<>(); // Initialize with an empty list
    }

    // Getters and Setters
    public int getProjectId() {
        return id;
    }

    public void setProjectId(int id) {
        this.id = id;
    }

    public String getProjectName() {
        return name;
    }

    public void setProjectName(String name) {
        this.name = name;
    }

    public Timestamp getProjectStart() {
        return start;
    }

    public void setProjectStart(Timestamp start) {
        this.start = start;
    }

    public Timestamp getProjectFinish() {
        return finish;
    }

    public void setProjectFinish(Timestamp finish) {
        this.finish = finish;
    }

    public Timestamp getProjectCreatedDate() {
        return created_date;
    }

    public void setProjectCreatedDate(Timestamp created_date) {
        this.created_date = created_date;
    }

    public Boolean getProjectIsActive() {
        return is_active;
    }

    public void setProjectIsActive(Boolean is_active) {
        this.is_active = is_active;
    }

    // Getter for tasks
    public List<Task> getTasks() {
        return tasks;
    }

    // Setter for tasks
    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public String toString() {
        return "Project{" +
                "projectId=" + id +
                ", name='" + name + '\'' +
                ", start=" + start +
                ", finish=" + finish +
                ", created_date=" + created_date +
                ", is_active=" + is_active +
                ", tasks=" + tasks + // Optionally include tasks in the string
                '}';
    }
}
