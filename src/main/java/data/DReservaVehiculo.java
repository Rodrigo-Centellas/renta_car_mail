// File: data/DReservaVehiculo.java
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

public class DReservaVehiculo {

    public static final String[] HEADERS = {
            "id", "fecha", "reserva_id", "vehiculo_id"
    };

    private final SqlConnection connection;

    public DReservaVehiculo() {
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
        String sql = "SELECT * FROM \"Reserva_vehiculo\" WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result.add(new String[]{
                            String.valueOf(rs.getInt("id")),
                            rs.getDate("fecha").toString(),
                            String.valueOf(rs.getInt("reserva_id")),
                            String.valueOf(rs.getInt("vehiculo_id"))
                    });
                }
            }
        }
        return result;
    }

    public List<String[]> save(Date fecha,
                               int reservaId,
                               int vehiculoId) throws SQLException {
        String sql = "INSERT INTO \"Reserva_vehiculo\" (fecha, reserva_id, vehiculo_id) "
                + "VALUES (?, ?, ?) RETURNING id";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, fecha);
            ps.setInt(2, reservaId);
            ps.setInt(3, vehiculoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int newId = rs.getInt(1);
                    return get(newId);
                }
            }
        }
        throw new SQLException("Error al insertar Reserva_vehiculo.");
    }

    public List<String[]> update(int id,
                                 Date fecha,
                                 int reservaId,
                                 int vehiculoId) throws SQLException {
        String sql = "UPDATE \"Reserva_vehiculo\" SET fecha = ?, reserva_id = ?, vehiculo_id = ? WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, fecha);
            ps.setInt(2, reservaId);
            ps.setInt(3, vehiculoId);
            ps.setInt(4, id);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Error al actualizar Reserva_vehiculo.");
            }
        }
        return get(id);
    }

    public List<String[]> delete(int id) throws SQLException {
        //List<String[]> remaining = list();
        String sql = "DELETE FROM \"Reserva_vehiculo\" WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Error al eliminar Reserva_vehiculo.");
            }
        }
        return list();
    }

    public List<String[]> list() throws SQLException {
        List<String[]> lista = new ArrayList<>();
        String sql = "SELECT * FROM \"Reserva_vehiculo\"";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getDate("fecha").toString(),
                        String.valueOf(rs.getInt("reserva_id")),
                        String.valueOf(rs.getInt("vehiculo_id"))
                });
            }
        }
        return lista;
    }

    public void disconnect() {
        connection.closeConnection();
    }
}
