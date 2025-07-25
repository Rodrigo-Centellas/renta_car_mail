// File: data/DContrato.java
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

public class DContrato {

    public static final String[] HEADERS = {
            "id",
            "estado",
            "fecha_inicio",
            "fecha_fin",
            "frecuencia_pago_id",
            "nro_cuenta_id",
            "vehiculo_id"
    };

    private final SqlConnection connection;

    public DContrato() {
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
        String sql = "SELECT * FROM \"Contrato\" WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result.add(new String[]{
                            String.valueOf(rs.getInt("id")),
                            rs.getString("estado"),
                            rs.getDate("fecha_inicio").toString(),
                            rs.getDate("fecha_fin").toString(),
                            String.valueOf(rs.getInt("frecuencia_pago_id")),
                            String.valueOf(rs.getInt("nro_cuenta_id")),
                            String.valueOf(rs.getInt("vehiculo_id"))
                    });
                }
            }
        }
        return result;
    }

    public List<String[]> save(String estado,
                               Date fechaInicio,
                               Date fechaFin,
                               int frecuenciaPagoId,
                               int nroCuentaId,
                               int vehiculoId) throws SQLException {
        String sql = "INSERT INTO \"Contrato\" " +
                "(estado, fecha_inicio, fecha_fin, frecuencia_pago_id, nro_cuenta_id, vehiculo_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setDate(2, fechaInicio);
            ps.setDate(3, fechaFin);
            ps.setInt(4, frecuenciaPagoId);
            ps.setInt(5, nroCuentaId);
            ps.setInt(6, vehiculoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int newId = rs.getInt(1);
                    return get(newId);
                }
            }
        }
        throw new SQLException("Error al insertar Contrato.");
    }

    public List<String[]> update(int id,
                                 String estado,
                                 Date fechaInicio,
                                 Date fechaFin,
                                 int frecuenciaPagoId,
                                 int nroCuentaId,
                                 int vehiculoId) throws SQLException {
        String sql = "UPDATE \"Contrato\" SET " +
                "estado = ?, fecha_inicio = ?, fecha_fin = ?, " +
                "frecuencia_pago_id = ?, nro_cuenta_id = ?, vehiculo_id = ?" +
                "WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setDate(2, fechaInicio);
            ps.setDate(3, fechaFin);
            ps.setInt(4, frecuenciaPagoId);
            ps.setInt(5, nroCuentaId);
            ps.setInt(6, vehiculoId);
            ps.setInt(7, id);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Error al actualizar Contrato.");
            }
        }
        return get(id);
    }

    public List<String[]> delete(int id) throws SQLException {
        //List<String[]> remaining = list();
        String sql = "DELETE FROM \"Contrato\" WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Error al eliminar Contrato.");
            }
        }
        return list();
    }

    public List<String[]> list() throws SQLException {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT * FROM \"Contrato\"";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("estado"),
                        rs.getDate("fecha_inicio").toString(),
                        rs.getDate("fecha_fin").toString(),
                        String.valueOf(rs.getInt("frecuencia_pago_id")),
                        String.valueOf(rs.getInt("nro_cuenta_id")),
                        String.valueOf(rs.getInt("vehiculo_id"))
                });
            }
        }
        return list;
    }

    public void disconnect() {
        connection.closeConnection();
    }
}
