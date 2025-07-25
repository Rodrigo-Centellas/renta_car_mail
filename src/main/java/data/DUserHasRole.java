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

    // Headers solo para los 3 campos de la tabla
    public static final String[] HEADERS = { "role_id", "model_type", "model_id" };

    // Constante para model_type fijo
    private static final String MODEL_TYPE = "App\\Models\\User";

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

    /**
     * Obtiene una asignación específica por role_id y user_id (model_id)
     */
    public List<String[]> get(int id) throws SQLException {
        // Interpretamos 'id' como user_id para obtener todos sus roles
        return getUserRoles(id);
    }

    /**
     * Asigna un rol a un usuario
     * Solo recibe user_id y role_id, model_type es fijo
     */
    public List<String[]> save(int userId, int roleId) throws SQLException {
        // Verificar si ya existe la asignación
        if (userHasRole(userId, roleId)) {
            throw new SQLException("El usuario ya tiene asignado este rol");
        }

        String sql = "INSERT INTO \"model_has_roles\" (role_id, model_type, model_id) VALUES (?, ?, ?)";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roleId);
            ps.setString(2, MODEL_TYPE);
            ps.setInt(3, userId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Error al insertar en model_has_roles.");
            }
        }
        return getUserRoles(userId);
    }

    /**
     * Actualiza una asignación de rol (NO APLICABLE - se elimina y se crea nueva)
     */
    public List<String[]> update(int id, int userId, int roleId) throws SQLException {
        // En esta tabla no hay UPDATE lógico, se elimina el rol anterior y se asigna el nuevo
        // Para simplicidad, interpretamos 'id' como el role_id anterior a cambiar
        delete(id, userId);
        return save(userId, roleId);
    }

    /**
     * Elimina una asignación de rol específica
     */
    public List<String[]> delete(int roleId, int userId) throws SQLException {
        String sql = "DELETE FROM \"model_has_roles\" WHERE role_id = ? AND model_id = ? AND model_type = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roleId);
            ps.setInt(2, userId);
            ps.setString(3, MODEL_TYPE);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("No se encontró la asignación de rol para eliminar.");
            }
        }
        return list();
    }

    /**
     * Sobrecarga para delete con un solo parámetro (elimina todos los roles del usuario)
     */
    public List<String[]> delete(int userId) throws SQLException {
        String sql = "DELETE FROM \"model_has_roles\" WHERE model_id = ? AND model_type = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, MODEL_TYPE);
            ps.executeUpdate();
        }
        return list();
    }

    /**
     * Lista todas las asignaciones de roles a usuarios
     */
    public List<String[]> list() throws SQLException {
        List<String[]> lista = new ArrayList<>();
        String sql = "SELECT role_id, model_type, model_id FROM \"model_has_roles\" WHERE model_type = ? ORDER BY model_id, role_id";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, MODEL_TYPE);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new String[]{
                            String.valueOf(rs.getInt("role_id")),
                            rs.getString("model_type"),
                            String.valueOf(rs.getInt("model_id"))
                    });
                }
            }
        }
        return lista;
    }

    /**
     * Obtiene todos los roles de un usuario específico
     */
    public List<String[]> getUserRoles(int userId) throws SQLException {
        List<String[]> lista = new ArrayList<>();
        String sql = "SELECT role_id, model_type, model_id FROM \"model_has_roles\" WHERE model_id = ? AND model_type = ? ORDER BY role_id";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, MODEL_TYPE);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new String[]{
                            String.valueOf(rs.getInt("role_id")),
                            rs.getString("model_type"),
                            String.valueOf(rs.getInt("model_id"))
                    });
                }
            }
        }
        return lista;
    }

    /**
     * Verifica si un usuario tiene un rol específico
     */
    public boolean userHasRole(int userId, int roleId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM \"model_has_roles\" WHERE model_id = ? AND role_id = ? AND model_type = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, roleId);
            ps.setString(3, MODEL_TYPE);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public void disconnect() {
        connection.closeConnection();
    }
}