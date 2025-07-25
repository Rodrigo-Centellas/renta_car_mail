// File: data/DVehiculoMantenimiento.java
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

public class DVehiculoMantenimiento {

    public static final String[] HEADERS = {
            "id", "fecha", "monto", "vehiculo_id", "mantenimiento_id"
    };

    private final SqlConnection connection;

    public DVehiculoMantenimiento() {
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
        String sql = "SELECT * FROM \"vehiculo_mantenimientos\" WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result.add(new String[]{
                            String.valueOf(rs.getInt("id")),
                            rs.getDate("fecha").toString(),
                            rs.getString("monto"),
                            String.valueOf(rs.getInt("vehiculo_id")),
                            String.valueOf(rs.getInt("mantenimiento_id"))
                    });
                }
            }
        }
        return result;
    }

    public List<String[]> save(Date fecha,
                               String monto,
                               int vehiculoId,
                               int mantenimientoId) throws SQLException {
        String sql = "INSERT INTO \"vehiculo_mantenimientos\" "
                + "(fecha, monto, vehiculo_id, mantenimiento_id) "
                + "VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, fecha);
            ps.setString(2, monto);
            ps.setInt(3, vehiculoId);
            ps.setInt(4, mantenimientoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int newId = rs.getInt(1);
                    return get(newId);
                }
            }
        }
        throw new SQLException("Error al insertar vehiculo_mantenimientos.");
    }

    public List<String[]> update(int id,
                                 Date fecha,
                                 String monto,
                                 int vehiculoId,
                                 int mantenimientoId) throws SQLException {
        String sql = "UPDATE \"vehiculo_mantenimientos\" SET "
                + "fecha = ?, monto = ?, vehiculo_id = ?, mantenimiento_id = ? "
                + "WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, fecha);
            ps.setString(2, monto);
            ps.setInt(3, vehiculoId);
            ps.setInt(4, mantenimientoId);
            ps.setInt(5, id);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Error al actualizar vehiculo_mantenimientos.");
            }
        }
        return get(id);
    }

    public List<String[]> delete(int id) throws SQLException {
        //List<String[]> remaining = list();
        String sql = "DELETE FROM \"vehiculo_mantenimientos\" WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Error al eliminar vehiculo_mantenimientos.");
            }
        }
        return list();
    }

    public List<String[]> list() throws SQLException {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT * FROM \"vehiculo_mantenimientos\"";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getDate("fecha").toString(),
                        rs.getString("monto"),
                        String.valueOf(rs.getInt("vehiculo_id")),
                        String.valueOf(rs.getInt("mantenimiento_id"))
                });
            }
        }
        return list;
    }

    public void disconnect() {
        connection.closeConnection();
    }
}
