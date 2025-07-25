// File: data/DUserContrato.java
package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

public class DUserContrato {

    public static final String[] HEADERS = {
            "id", "user_id", "contrato_id"
    };

    private final SqlConnection connection;

    public DUserContrato() {
        this.connection = new SqlConnection(
                DBConnection.database,
                DBConnection.server,
                DBConnection.port,
                DBConnection.user,
                DBConnection.password
        );
    }

    public List<String[]> get(int id) throws SQLException {
        List<String[]> result = new ArrayList<>();
        String sql = "SELECT * FROM \"user_contratos\" WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result.add(new String[]{
                            String.valueOf(rs.getInt("id")),
                            String.valueOf(rs.getInt("user_id")),
                            String.valueOf(rs.getInt("contrato_id"))
                    });
                }
            }
        }
        return result;
    }

    public List<String[]> save(int userId, int contratoId) throws SQLException {
        String sql = "INSERT INTO \"user_contratos\" (user_id, contrato_id) VALUES (?, ?) RETURNING id";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, contratoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return get(rs.getInt(1));
                }
            }
        }
        throw new SQLException("Error al insertar user_contratos.");
    }

    public List<String[]> update(int id, int userId, int contratoId) throws SQLException {
        String sql = "UPDATE \"user_contratos\" SET user_id = ?, contrato_id = ? WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, contratoId);
            ps.setInt(3, id);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Error al actualizar user_contratos.");
            }
        }
        return get(id);
    }

    public List<String[]> delete(int id) throws SQLException {
        //List<String[]> remaining = list();
        String sql = "DELETE FROM \"user_contratos\" WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Error al eliminar user_contratos.");
            }
        }
        return list();
    }

    public List<String[]> list() throws SQLException {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT * FROM \"user_contratos\"";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("user_id")),
                        String.valueOf(rs.getInt("contrato_id"))
                });
            }
        }
        return list;
    }

    public void disconnect() {
        connection.closeConnection();
    }
}
