// File: data/DGarante.java
package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

public class DGarante {

    public static final String[] HEADERS = {
            "id", "apellido", "ci", "domicilio", "nombre", "telefono"
    };

    private final SqlConnection connection;

    public DGarante() {
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
        String query = "SELECT * FROM \"Garante\" WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    resultado.add(new String[]{
                            String.valueOf(rs.getInt("id")),
                            rs.getString("apellido"),
                            String.valueOf(rs.getInt("ci")),
                            rs.getString("domicilio"),
                            rs.getString("nombre"),
                            String.valueOf(rs.getInt("telefono"))
                    });
                }
            }
        }
        return resultado;
    }

    public List<String[]> save(String apellido,
                               int ci,
                               String domicilio,
                               String nombre,
                               int telefono) throws SQLException {
        String query = "INSERT INTO \"Garante\" (apellido, ci, domicilio, nombre, telefono) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING id";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, apellido);
            ps.setInt(2, ci);
            ps.setString(3, domicilio);
            ps.setString(4, nombre);
            ps.setInt(5, telefono);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int newId = rs.getInt(1);
                    return get(newId);
                }
            }
        }
        throw new SQLException("Error al insertar Garante.");
    }

    public List<String[]> update(int id,
                                 String apellido,
                                 int ci,
                                 String domicilio,
                                 String nombre,
                                 int telefono) throws SQLException {
        String query = "UPDATE \"Garante\" SET apellido = ?, ci = ?, domicilio = ?, nombre = ?, telefono = ? " +
                "WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, apellido);
            ps.setInt(2, ci);
            ps.setString(3, domicilio);
            ps.setString(4, nombre);
            ps.setInt(5, telefono);
            ps.setInt(6, id);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Error al actualizar Garante.");
            }
        }
        return get(id);
    }

    public List<String[]> delete(int id) throws SQLException {
        // List<String[]> restantes = list();
        String query = "DELETE FROM \"Garante\" WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Error al eliminar Garante.");
            }
        }
        return list();
    }

    public List<String[]> list() throws SQLException {
        List<String[]> lista = new ArrayList<>();
        String query = "SELECT * FROM \"Garante\"";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("apellido"),
                        String.valueOf(rs.getInt("ci")),
                        rs.getString("domicilio"),
                        rs.getString("nombre"),
                        String.valueOf(rs.getInt("telefono"))
                });
            }
        }
        return lista;
    }

    public void disconnect() {
        connection.closeConnection();
    }
}
