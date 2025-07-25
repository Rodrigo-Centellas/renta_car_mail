// File: data/DUserHasRole.java
package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

public class DUserHasRole {

    public static final String[] HEADERS = { "id", "user_id", "role_id" };

    private final SqlConnection connection;

    public DUserHasRole() {
        this.connection = new SqlConnection(
                DBConnection.database,
                DBConnection.server,
                DBConnection.port,
                DBConnection.user,
                DBConnection.password
        );
    }

    public List<String[]> get(int id) throws SQLException {
        List<String[]> resultado = new ArrayList<>();
        String sql = "SELECT * FROM \"UserHasRole\" WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    resultado.add(new String[]{
                            String.valueOf(rs.getInt("id")),
                            String.valueOf(rs.getInt("user_id")),
                            String.valueOf(rs.getInt("role_id"))
                    });
                }
            }
        }
        return resultado;
    }

    public List<String[]> save(int userId, int roleId) throws SQLException {
        String sql = "INSERT INTO \"UserHasRole\" (user_id, role_id) VALUES (?, ?) RETURNING id";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, roleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int newId = rs.getInt(1);
                    return get(newId);
                }
            }
        }
        throw new SQLException("Error al insertar UserHasRole.");
    }

    public List<String[]> update(int id, int userId, int roleId) throws SQLException {
        String sql = "UPDATE \"UserHasRole\" SET user_id = ?, role_id = ? WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, roleId);
            ps.setInt(3, id);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Error al actualizar UserHasRole.");
            }
        }
        return get(id);
    }

    public List<String[]> delete(int id) throws SQLException {
        String sql = "DELETE FROM \"UserHasRole\" WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Error al eliminar UserHasRole.");
            }
        }
        return list();
    }

    public List<String[]> list() throws SQLException {
        List<String[]> lista = new ArrayList<>();
        String sql = "SELECT * FROM \"UserHasRole\"";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("user_id")),
                        String.valueOf(rs.getInt("role_id"))
                });
            }
        }
        return lista;
    }

    public void disconnect() {
        connection.closeConnection();
    }
}
