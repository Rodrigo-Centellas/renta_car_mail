// File: data/DVehiculo.java
package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

public class DVehiculo {

    public static final String[] HEADERS = {
            "id", "estado", "monto_garantia", "precio_dia", "tipo"
    };

    private final SqlConnection connection;

    public DVehiculo() {
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
        String query = "SELECT * FROM \"Vehiculo\" WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    resultado.add(new String[]{
                            String.valueOf(rs.getInt("id")),
                            rs.getString("estado"),
                            String.valueOf(rs.getFloat("monto_garantia")),
                            String.valueOf(rs.getFloat("precio_dia")),
                            rs.getString("tipo")
                    });
                }
            }
        }
        return resultado;
    }

    public List<String[]> save(String estado,
                               float montoGarantia,
                               float precioDia,
                               String tipo) throws SQLException {
        String query = "INSERT INTO \"Vehiculo\" " +
                "(estado, monto_garantia, precio_dia, tipo) " +
                "VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, estado);
            ps.setFloat(2, montoGarantia);
            ps.setFloat(3, precioDia);
            ps.setString(4, tipo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int newId = rs.getInt(1);
                    return get(newId);
                }
            }
        }
        throw new SQLException("Error al insertar Vehiculo.");
    }

    public List<String[]> update(int id,
                                 String estado,
                                 float montoGarantia,
                                 float precioDia,
                                 String tipo) throws SQLException {
        String query = "UPDATE \"Vehiculo\" SET estado = ?, monto_garantia = ?, precio_dia = ?, tipo = ? " +
                "WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, estado);
            ps.setFloat(2, montoGarantia);
            ps.setFloat(3, precioDia);
            ps.setString(4, tipo);
            ps.setInt(5, id);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Error al actualizar Vehiculo.");
            }
        }
        return get(id);
    }

    public List<String[]> delete(int id) throws SQLException {
        //List<String[]> restantes = list();
        String query = "DELETE FROM \"Vehiculo\" WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Error al eliminar Vehiculo.");
            }
        }
        return list();
    }

    public List<String[]> list() throws SQLException {
        List<String[]> lista = new ArrayList<>();
        String query = "SELECT * FROM \"Vehiculo\"";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("estado"),
                        String.valueOf(rs.getFloat("monto_garantia")),
                        String.valueOf(rs.getFloat("precio_dia")),
                        rs.getString("tipo")
                });
            }
        }
        return lista;
    }

    public void disconnect() {
        connection.closeConnection();
    }
}
