package org.example.dao;

import org.example.model.Assignment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class AssignmentDao {
    private Connection connection;

    public AssignmentDao(Connection connection) {
        this.connection = connection;
    }

    public void insertAssignments(List<Assignment> assignments) throws SQLException {
        String sql = "INSERT INTO z_assignment (id, task_id, resource_id, etc, actuals) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (Assignment assignment : assignments) {
                ps.setInt(1,assignment.getAssignmentId());
                ps.setInt(2, assignment.getTaskId());
                ps.setInt(3, assignment.getResourceId());
                ps.setBigDecimal(4, assignment.getEtc());
                ps.setBigDecimal(5, assignment.getActuals());
                ps.executeUpdate();
            }
        }
    }
}

