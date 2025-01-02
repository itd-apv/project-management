package org.example.model;

public class Resource {
    private int id;
    private String name;
    private Boolean is_active;

    public Resource(int id, String name, Boolean is_active) {
        this.id = id;
        this.name = name;
        this.is_active = is_active;
    }

    // Getters and Setters
    public int getResourceId() {
        return id;
    }
    public void setResourceId(int resourceId) {
        this.id = resourceId;
    }

    public String getResourceName() {
        return name;
    }
    public void setResourceName(String resourceName) {
        this.name = resourceName;
    }

    public Boolean getResourceIsActive() {
        return is_active;
    }
    public void setResourceIsActive(Boolean is_active) {
        this.is_active = is_active;
    }

    @Override
    public String toString() {
        return "Resource{" +
                "resourceId=" + id +
                ", name=" + name +
                ", is_active=" + is_active +
                '}';
    }
}