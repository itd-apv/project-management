package org.example.dao;

import org.example.model.Resource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ResourceDao {
    private Connection connection;

    public ResourceDao(Connection connection) {
        this.connection = connection;
    }

    public void insertResources(List<Resource> resources) throws SQLException {
        String sql = "INSERT INTO z_resource (id, name, is_active) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (Resource resource : resources) {
                ps.setInt(1,resource.getResourceId());
                ps.setString(2, resource.getResourceName());
                ps.setBoolean(3, resource.getResourceIsActive());
                ps.executeUpdate();
            }
        }
    }

    public boolean doesResourceExist(int resourceId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM z_resource WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, resourceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}