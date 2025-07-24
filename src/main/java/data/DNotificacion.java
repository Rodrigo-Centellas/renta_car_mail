// File: data/DNotificacion.java
package data;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

public class DNotificacion {

    public static final String[] HEADERS = {
            "id", "fecha", "mensaje", "tipo", "user_id"
    };

    private final SqlConnection connection;

    public DNotificacion() {
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
        String sql = "SELECT * FROM \"Notificacion\" WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result.add(new String[]{
                            String.valueOf(rs.getInt("id")),
                            rs.getDate("fecha").toString(),
                            rs.getString("mensaje"),
                            rs.getString("tipo"),
                            String.valueOf(rs.getInt("user_id"))
                    });
                }
            }
        }
        return result;
    }

    public List<String[]> save(Date fecha,
                               String mensaje,
                               String tipo,
                               int userId) throws SQLException {
        String sql = "INSERT INTO \"Notificacion\" (fecha, mensaje, tipo, user_id) "
                + "VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, fecha);
            ps.setString(2, mensaje);
            ps.setString(3, tipo);
            ps.setInt(4, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int newId = rs.getInt(1);
                    return get(newId);
                }
            }
        }
        throw new SQLException("Error al insertar Notificacion.");
    }

    public List<String[]> update(int id,
                                 Date fecha,
                                 String mensaje,
                                 String tipo,
                                 int userId) throws SQLException {
        String sql = "UPDATE \"Notificacion\" SET fecha = ?, mensaje = ?, tipo = ?, user_id = ? WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, fecha);
            ps.setString(2, mensaje);
            ps.setString(3, tipo);
            ps.setInt(4, userId);
            ps.setInt(5, id);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Error al actualizar Notificacion.");
            }
        }
        return get(id);
    }

    public List<String[]> delete(int id) throws SQLException {
        //List<String[]> remaining = list();
        String sql = "DELETE FROM \"Notificacion\" WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Error al eliminar Notificacion.");
            }
        }
        return list();
    }

    public List<String[]> list() throws SQLException {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT * FROM \"Notificacion\"";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getDate("fecha").toString(),
                        rs.getString("mensaje"),
                        rs.getString("tipo"),
                        String.valueOf(rs.getInt("user_id"))
                });
            }
        }
        return list;
    }

    public void disconnect() {
        connection.closeConnection();
    }
}
