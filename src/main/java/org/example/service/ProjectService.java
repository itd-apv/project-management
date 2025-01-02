package org.example.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.dao.ProjectDao;
import org.example.dao.TaskDao;
import org.example.model.Project;
import org.example.model.Task;
import java.sql.SQLException;
import java.util.*;

public class ProjectService {
    private static final Logger logger = LogManager.getLogger(ProjectService.class);
    private ProjectDao projectDao;
    private TaskDao taskDao;

    public ProjectService(ProjectDao projectDao,TaskDao taskDao) {
        this.projectDao = projectDao;
        this.taskDao = taskDao;
    }

    public void validateProjects(List<Project> projects) throws SQLException {
        List<Project> validProjects = new ArrayList<>();
        Set<Integer> projectIdSet = new HashSet<>();
        int lineNumber = 1;

        for (Project project : projects) {
            if (project == null) {
                logger.warn("Skipping null project on line {}", lineNumber);
                lineNumber++;
                continue;

            }

            if (isProjectValid(project, projectIdSet, lineNumber)) {
                validProjects.add(project);
            } else {
                logger.debug("Project failed validation: {}", project);
            }
            lineNumber++;
        }

        if (!validProjects.isEmpty()) {
            projectDao.insertProjects(validProjects);
            logger.info("Successfully inserted {} valid projects into the database.", validProjects.size());
        } else {
            logger.warn("No valid projects to insert.");
        }
    }

    private boolean isProjectValid(Project project, Set<Integer> projectIdSet, int lineNumber) {

        return isProjectNameValid(project, lineNumber) &&
                isProjectStartValid(project, lineNumber) &&
                isProjectFinishValid(project, lineNumber) &&
                isProjectCreatedDateValid(project, lineNumber) &&
                isProjectActiveValid(project, lineNumber) &&
                isProjectIdValid(project, projectIdSet, lineNumber);
    }

    private boolean isProjectIdValid(Project project, Set<Integer> projectIdSet, int lineNumber) {
        if (project.getProjectId() <= 0) {
            logger.error("Invalid Project ID on Line: {}. Skipping this entry.", lineNumber);
            return false;
        }
        if (projectIdSet.contains(project.getProjectId())) {
            logger.error("Duplicate Project ID on Line: {}. Skipping this entry.", lineNumber);
            return false;
        }
        projectIdSet.add(project.getProjectId());
        return true;
    }

    private boolean isProjectNameValid(Project project, int lineNumber) {
        if (project.getProjectName() == null || project.getProjectName().trim().isEmpty()) {
            logger.error("Invalid Project Name on Line: {}. Skipping this entry.", lineNumber);
            return false;
        }
        return true;
    }

    private boolean isProjectStartValid(Project project, int lineNumber) {
        if (project.getProjectStart() == null) {
            logger.error("Invalid Project Start on Line: {}. Skipping this entry.", lineNumber);
            return false;
        }
        return true;
    }

    private boolean isProjectFinishValid(Project project, int lineNumber) {
        if (project.getProjectFinish() == null) {
            logger.error("Invalid Project Finish on Line: {}. Skipping this entry.", lineNumber);
            return false;
        }
        return true;
    }

    private boolean isProjectCreatedDateValid(Project project, int lineNumber) {
        if (project.getProjectCreatedDate() == null) {
            logger.error("Invalid Project Created Date on Line: {}. Skipping this entry.", lineNumber);
            return false;
        }
        return true;
    }

    private boolean isProjectActiveValid(Project project, int lineNumber) {
        if (project.getProjectIsActive() == null) {
            logger.error("Invalid Is Active on Line: {}. Skipping this entry.", lineNumber);
            return false;
        }
        return true;
    }

    //    public Project getProjectWithTasks(int projectId) throws SQLException {
//        logger.info("Fetching project with ID {} along with its tasks.", projectId);
//
//        // Use the DAO method to fetch the project and its tasks
//        Project project = projectDao.findByIdWithTasks(projectId);
//
//        if (project == null) {
//            logger.warn("Project with ID {} not found.", projectId);
//            throw new SQLException("Project with ID " + projectId + " does not exist.");
//        }
//
//        // Log the project details to the console
//        logger.info("Successfully fetched project with tasks: {}", project);
//
//        // If you want to print the project details explicitly
//        System.out.println("Project Details:");
//        System.out.println("Project ID: " + project.getProjectId());
//        System.out.println("Project Name: " + project.getProjectName());
//
//        // Add more fields if necessary
//        // Example: System.out.println("Project Description: " + project.getDescription());
//
//        // If your Project class has a method to get a list of tasks, you can print them as well
//        if (project.getTasks() != null && !project.getTasks().isEmpty()) {
//            System.out.println("Tasks associated with this project:");
//            for (Task task : project.getTasks()) {
//                System.out.println("Task ID: " + task.getTaskId() + ", Task Name: " + task.getTaskName() + ", Status: " + task.getTaskStatus());
//            }
//        } else {
//            System.out.println("No tasks associated with this project.");
//        }
//
//        return project;
//    }
//    public void addTaskToProject(int projectId, Task task) throws SQLException {
//        Project project = projectDao.findById(projectId);
//        if (project != null) {
//            // Logic to add task to the project
//            taskDao.addTask(task); // Assuming you have an addTask method in your TaskDao
//            project.getTasks().add(task); // Update the project's task list
//        } else {
//            throw new IllegalArgumentException("Project not found");
//        }
//    }
//    public String updateTaskField(int taskId, String fieldName, Object fieldValue) {
//        // Validate the field name to prevent SQL injection
//        List<String> validFields = Arrays.asList("name", "project_id", "status"); // Valid fields for your table
//        if (!validFields.contains(fieldName)) {
//            return "Invalid field name";
//        }
//
//        // Attempt to update the field using DAO
//        boolean isUpdated = taskDao.updateTaskField(taskId, fieldName, fieldValue);
//        if (isUpdated) {
//            return "Task updated successfully";
//        } else {
//            return "Failed to update task or task not found";
//        }
//    }
    public Project getProjectWithTasks(int projectId) throws SQLException {
        logger.info("Fetching project with ID {} along with its tasks.", projectId);

        Project project = projectDao.findByIdWithTasks(projectId);

        if (project == null) {
            logger.warn("Project with ID {} not found.", projectId);
            throw new SQLException("Project with ID " + projectId + " does not exist.");
        }

        logger.info("Successfully fetched project with tasks: {}", project);
        logProjectDetails(project);

        return project;
    }

    private void logProjectDetails(Project project) {
        logger.info("Project Details - ID: {}, Name: {}", project.getProjectId(), project.getProjectName());
        if (project.getTasks() != null && !project.getTasks().isEmpty()) {
            for (Task task : project.getTasks()) {
                logger.info("Task Details - ID: {}, Name: {}, Status: {}", task.getTaskId(), task.getTaskName(), task.getTaskStatus());
            }
        } else {
            logger.info("No tasks associated with this project.");
        }
    }

    public void addTaskToProject(int projectId, Task task) throws SQLException {
        logger.info("Adding task to project. Project ID: {}, Task: {}", projectId, task);
        Project project = projectDao.findById(projectId);

        if (project != null) {
            taskDao.addTask(task); // Assuming you have an addTask method in your TaskDao
            project.getTasks().add(task); // Update the project's task list
            logger.info("Task successfully added to project with ID {}. Task Details: {}", projectId, task);
        } else {
            logger.error("Project with ID {} not found. Unable to add task.", projectId);
            throw new IllegalArgumentException("Project not found");
        }
    }

    public String updateTaskField(int taskId, String fieldName, Object fieldValue) {
        logger.info("Updating task field. Task ID: {}, Field Name: {}, Field Value: {}", taskId, fieldName, fieldValue);

        List<String> validFields = Arrays.asList("name", "project_id", "status");
        if (!validFields.contains(fieldName)) {
            logger.error("Invalid field name provided: {}", fieldName);
            return "Invalid field name";
        }

        boolean isUpdated = taskDao.updateTaskField(taskId, fieldName, fieldValue);

        if (isUpdated) {
            logger.info("Task with ID {} successfully updated. Updated Field: {}, New Value: {}", taskId, fieldName, fieldValue);
            return "Task updated successfully";
        } else {
            logger.warn("Failed to update task with ID {}. Either the task does not exist or the update failed.", taskId);
            return "Failed to update task or task not found";
        }
    }
}