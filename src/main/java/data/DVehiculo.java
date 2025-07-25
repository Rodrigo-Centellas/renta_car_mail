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

    // HEADERS reordenados según el orden de la base de datos
    public static final String[] HEADERS = {
            "id", "placa", "marca", "modelo", "url_imagen",
            "estado", "monto_garantia", "precio_dia", "tipo"
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
                rs.getString("placa"),
                rs.getString("marca"),
                rs.getString("modelo"),
                rs.getString("url_imagen"),
                rs.getString("estado"),
                String.valueOf(rs.getFloat("monto_garantia")),
                String.valueOf(rs.getFloat("precio_dia")),
                rs.getString("tipo")
        };
    }

    public List<String[]> get(int id) throws SQLException {
        List<String[]> resultado = new ArrayList<>();
        String query = "SELECT * FROM vehiculos WHERE id = ?";
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
                               String urlImagen) throws SQLException {
        String query = "INSERT INTO vehiculos " +
                "(estado, marca, modelo, monto_garantia, placa, precio_dia, tipo, url_imagen) " +
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
            ps.setString(8, urlImagen);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int newId = rs.getInt(1);
                    return get(newId);
                }
            }
        }
        throw new SQLException("Error al insertar vehículo.");
    }

    public List<String[]> update(int id,
                                 String estado,
                                 String marca,
                                 String modelo,
                                 float montoGarantia,
                                 String placa,
                                 float precioDia,
                                 String tipo,
                                 String urlImagen) throws SQLException {
        String query = "UPDATE vehiculos SET " +
                "estado = ?, marca = ?, modelo = ?, monto_garantia = ?, placa = ?, precio_dia = ?, tipo = ?, url_imagen = ? " +
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
            ps.setString(8, urlImagen);
            ps.setInt(9, id);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Error al actualizar vehículo.");
            }
        }
        return get(id);
    }

    public List<String[]> delete(int id) throws SQLException {
        String query = "DELETE FROM vehiculos WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Error al eliminar vehículo.");
            }
        }
        return list();
    }

    public List<String[]> list() throws SQLException {
        List<String[]> lista = new ArrayList<>();
        String query = "SELECT * FROM vehiculos";
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