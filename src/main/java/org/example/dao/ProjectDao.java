package org.example.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Project;
import org.example.model.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


public class ProjectDao {
    private static final Logger logger = LogManager.getLogger(ProjectDao.class);
    private Connection connection;

    public ProjectDao(Connection connection) {
        this.connection = connection;
    }

    public void insertProjects(List<Project> projects) throws SQLException {
        String sql = "INSERT INTO z_project (id, name, start, finish, created_date, is_active) VALUES (?, ?, ?, ?, ?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (Project project : projects) {
                ps.setInt(1, project.getProjectId());
                ps.setString(2, project.getProjectName());
                ps.setTimestamp(3, project.getProjectStart());
                ps.setTimestamp(4, project.getProjectFinish());
                ps.setTimestamp(5, project.getProjectCreatedDate());
                ps.setBoolean(6, project.getProjectIsActive());
                ps.executeUpdate();
            }
        }
    }

    public boolean doesProjectExist(int projectId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM z_project WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, projectId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public Project findById(int projectId) throws SQLException {
        String sql = "SELECT * FROM z_project WHERE id = ?";
        Project project = null;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, projectId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    project = new Project(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getTimestamp("start"),
                            rs.getTimestamp("finish"),
                            rs.getTimestamp("created_date"),
                            rs.getBoolean("is_active")
                    );
                }
            }
        }
        return project;
    }

    public Project findByIdWithTasks(int projectId) throws SQLException {
        Project project = null;

        logger.info("Fetching project with ID: {}", projectId); // Log project retrieval start

        // Step 1: Retrieve the project details
        String projectSql = "SELECT * FROM z_project WHERE id = ?";
        try (PreparedStatement projectStmt = connection.prepareStatement(projectSql)) {
            projectStmt.setInt(1, projectId);
            logger.debug("Executing SQL: {}", projectSql); // Log the SQL query

            try (ResultSet rs = projectStmt.executeQuery()) {
                if (rs.next()) {
                    // Create the project object
                    project = new Project(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getTimestamp("start"),
                            rs.getTimestamp("finish"),
                            rs.getTimestamp("created_date"),
                            rs.getBoolean("is_active")
                            // Add other fields as necessary
                    );
                    logger.info("Successfully retrieved project: {}", project); // Log successful retrieval

                    // Step 2: Retrieve the associated tasks
                    List<Task> tasks = getTasksForProject(projectId);
                    project.setTasks(tasks); // Assuming there's a method to set tasks
                    logger.info("Retrieved {} tasks for project ID: {}", tasks.size(), projectId); // Log number of tasks
                } else {
                    logger.warn("No project found with ID: {}", projectId); // Log if project is not found
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving project with ID {}: {}", projectId, e.getMessage()); // Log SQL errors
            throw e; // Rethrow exception after logging
        }

        return project; // Return the project, which may have tasks set
    }

    private List<Task> getTasksForProject(int projectId) throws SQLException {
        List<Task> tasks = new ArrayList<>();

        logger.info("Fetching tasks for project ID: {}", projectId); // Log task retrieval start

        // SQL query to get tasks related to the project
        String tasksSql = "SELECT * FROM z_task WHERE project_id = ?";
        try (PreparedStatement tasksStmt = connection.prepareStatement(tasksSql)) {
            tasksStmt.setInt(1, projectId);
            logger.debug("Executing SQL: {}", tasksSql); // Log the SQL query

            try (ResultSet tasksRs = tasksStmt.executeQuery()) {
                while (tasksRs.next()) {
                    // Create a Task object for each row
                    Task task = new Task(
                            tasksRs.getInt("id"),
                            tasksRs.getString("name"),
                            tasksRs.getInt("project_id"),
                            tasksRs.getString("status")
                    );
                    tasks.add(task);
                    logger.debug("Retrieved task: {}", task); // Log each retrieved task
                }
                logger.info("Total tasks retrieved for project ID {}: {}", projectId, tasks.size()); // Log total tasks retrieved
            }
        } catch (SQLException e) {
            logger.error("Error retrieving tasks for project ID {}: {}", projectId, e.getMessage()); // Log SQL errors
            throw e; // Rethrow exception after logging
        }

        return tasks; // Return the list of tasks
    }
}
