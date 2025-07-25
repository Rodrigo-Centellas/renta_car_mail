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
            "id", "estado", "marca", "modelo", "monto_garantia",
            "placa", "precio_dia", "tipo", "url_foto"
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

    private String[] mapRow(ResultSet rs) throws SQLException {
        return new String[]{
                String.valueOf(rs.getInt("id")),
                rs.getString("estado"),
                rs.getString("marca"),
                rs.getString("modelo"),
                String.valueOf(rs.getFloat("monto_garantia")),
                rs.getString("placa"),
                String.valueOf(rs.getFloat("precio_dia")),
                rs.getString("tipo"),
                rs.getString("url_foto")
        };
    }

    public List<String[]> get(int id) throws SQLException {
        List<String[]> resultado = new ArrayList<>();
        String query = "SELECT * FROM \"Vehiculo\" WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    resultado.add(mapRow(rs));
                }
            }
        }
        return resultado;
    }

    public List<String[]> save(String estado,
                               String marca,
                               String modelo,
                               float montoGarantia,
                               String placa,
                               float precioDia,
                               String tipo,
                               String urlFoto) throws SQLException {
        String query = "INSERT INTO \"Vehiculo\" " +
                "(estado, marca, modelo, monto_garantia, placa, precio_dia, tipo, url_foto) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, estado);
            ps.setString(2, marca);
            ps.setString(3, modelo);
            ps.setFloat(4, montoGarantia);
            ps.setString(5, placa);
            ps.setFloat(6, precioDia);
            ps.setString(7, tipo);
            ps.setString(8, urlFoto);
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
                                 String marca,
                                 String modelo,
                                 float montoGarantia,
                                 String placa,
                                 float precioDia,
                                 String tipo,
                                 String urlFoto) throws SQLException {
        String query = "UPDATE \"Vehiculo\" SET " +
                "estado = ?, marca = ?, modelo = ?, monto_garantia = ?, placa = ?, precio_dia = ?, tipo = ?, url_foto = ? " +
                "WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, estado);
            ps.setString(2, marca);
            ps.setString(3, modelo);
            ps.setFloat(4, montoGarantia);
            ps.setString(5, placa);
            ps.setFloat(6, precioDia);
            ps.setString(7, tipo);
            ps.setString(8, urlFoto);
            ps.setInt(9, id);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Error al actualizar Vehiculo.");
            }
        }
        return get(id);
    }

    public List<String[]> delete(int id) throws SQLException {
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
                lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    public void disconnect() {
        connection.closeConnection();
    }
}
