package org.example.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class TaskDao {
    private static final Logger logger = LogManager.getLogger(TaskDao.class);
    private Connection connection;

    public TaskDao(Connection connection) {
        this.connection = connection;
    }

    public void insertTasks(List<Task> tasks) throws SQLException {
        String sql = "INSERT INTO z_task (id, name, project_id, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (Task task : tasks) {
                if (!doesTaskExist(task.getTaskId())) {
                    ps.setInt(1, task.getTaskId());
                    ps.setString(2, task.getTaskName());
                    ps.setInt(3, task.getProjectId());
                    ps.setString(4, task.getTaskStatus());
                    ps.addBatch();
                }
            }
            ps.executeBatch(); // Execute all batched statements in one go
        }
    }

    public boolean doesTaskExist(int taskId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM z_task WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, taskId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public void addTask(Task task) throws SQLException {
        String sql = "INSERT INTO z_task (id,name, project_id, status) VALUES (?,?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, task.getTaskId());
            pstmt.setString(2, task.getTaskName());
            pstmt.setInt(3, task.getProjectId());
            pstmt.setString(4, task.getTaskStatus());
            pstmt.executeUpdate();
        }
    }
    public boolean updateTaskField(int taskId, String fieldName, Object fieldValue) {
        // Validate the field name to prevent SQL injection
        List<String> validFields = Arrays.asList("name", "project_id", "status"); // Valid fields for your table
        if (!validFields.contains(fieldName)) {
            logger.error("Attempted to update invalid field: {}", fieldName);
            return false; // Return false if the field name is invalid
        }

        // Prepare the SQL query
        String query = "UPDATE z_task SET " + fieldName + " = ? WHERE id = ?"; // Ensure you're using the correct column name
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, fieldValue);
            statement.setInt(2, taskId);

            logger.info("Executing SQL: {} with value: {} for task ID: {}", query, fieldValue, taskId);

            int rowsUpdated = statement.executeUpdate();
            logger.info("Rows updated: {}", rowsUpdated);
            return rowsUpdated > 0; // Returns true if at least one row was updated
        } catch (SQLException e) {
            logger.error("Error updating task field: {}. SQLState: {}. Error Code: {}", e.getMessage(), e.getSQLState(), e.getErrorCode());
        }
        return false;
    }
}
