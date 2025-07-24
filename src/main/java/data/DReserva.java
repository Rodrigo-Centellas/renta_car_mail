// File: data/DReserva.java
package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;
import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

public class DReserva {

    public static final String[] HEADERS = {
            "id", "estado", "fecha", "vehiculo_id", "user_id"
    };

    private final SqlConnection connection;

    public DReserva() {
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
        String sql = "SELECT * FROM \"Reserva\" WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // usamos Timestamp para la columna fecha
                    Timestamp ts = rs.getTimestamp("fecha");
                    result.add(new String[]{
                            String.valueOf(rs.getInt("id")),
                            rs.getString("estado"),
                            ts != null ? ts.toString() : null,
                            String.valueOf(rs.getInt("vehiculo_id")),
                            String.valueOf(rs.getInt("user_id"))
                    });
                }
            }
        }
        return result;
    }

    /**
     * Ahora sólo recibimos: estado, vehiculo_id y user_id.
     * La fecha la pone Postgres con CURRENT_TIMESTAMP.
     */
    public List<String[]> save(String estado,
                               int vehiculoId,
                               int userId) throws SQLException {
        String sql = "INSERT INTO \"Reserva\" " +
                "(estado, fecha, vehiculo_id, user_id) " +
                "VALUES (?, CURRENT_TIMESTAMP, ?, ?) RETURNING id";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, vehiculoId);
            ps.setInt(3, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return get(rs.getInt(1));
                }
            }
        }
        throw new SQLException("Error al insertar Reserva.");
    }

    /**
     * No tocamos la fecha al actualizar.
     */
    public List<String[]> update(int id,
                                 String estado,
                                 int vehiculoId,
                                 int userId) throws SQLException {
        String sql = "UPDATE \"Reserva\" SET " +
                "estado = ?, vehiculo_id = ?, user_id = ? " +
                "WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, vehiculoId);
            ps.setInt(3, userId);
            ps.setInt(4, id);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Error al actualizar Reserva.");
            }
        }
        return get(id);
    }

    public List<String[]> delete(int id) throws SQLException {
        //List<String[]> remaining = list();
        String sql = "DELETE FROM \"Reserva\" WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Error al eliminar Reserva.");
            }
        }
        return list();
    }

    public List<String[]> list() throws SQLException {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT * FROM \"Reserva\"";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("fecha");
                list.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("estado"),
                        ts != null ? ts.toString() : null,
                        String.valueOf(rs.getInt("vehiculo_id")),
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
