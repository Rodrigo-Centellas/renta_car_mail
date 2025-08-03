// File: data/DNroCuenta.java
package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

public class DNroCuenta {

    public static final String[] HEADERS = {"id", "banco", "nro_cuenta", "es_activa"};

    private final SqlConnection connection;

    public DNroCuenta() {
        this.connection = new SqlConnection(
                DBConnection.database,
                DBConnection.server,
                DBConnection.port,
                DBConnection.user,
                DBConnection.password
        );
    }

    /**
     * Convierte un string a boolean de manera segura
     */
    private boolean parseBoolean(String value) {
        if (value == null) return false;

        // Normalizar el string
        String normalized = value.trim().toLowerCase();

        // Valores que se consideran true
        return normalized.equals("true") ||
                normalized.equals("1") ||
                normalized.equals("yes") ||
                normalized.equals("sí") ||
                normalized.equals("si") ||
                normalized.equals("verdadero") ||
                normalized.equals("activa") ||
                normalized.equals("activo");
    }

    public List<String[]> get(int id) throws SQLException {
        List<String[]> resultado = new ArrayList<>();
        String query = "SELECT * FROM nro_cuentas WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    resultado.add(new String[]{
                            String.valueOf(rs.getInt("id")),
                            rs.getString("banco"),
                            String.valueOf(rs.getInt("nro_cuenta")),
                            String.valueOf(rs.getBoolean("es_activa")) // Ya estaba correcto
                    });
                }
            }
        }
        return resultado;
    }

    public List<String[]> save(String banco, int nroCuenta, boolean esActiva) throws SQLException {
        System.out.println("🔧 Creando cuenta bancaria:");
        System.out.println("   Banco: " + banco);
        System.out.println("   Número cuenta: " + nroCuenta);
        System.out.println("   Es activa: " + esActiva);

        String query = "INSERT INTO nro_cuentas (banco, nro_cuenta, es_activa) VALUES (?, ?, ?) RETURNING id";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, banco);
            ps.setInt(2, nroCuenta);
            ps.setBoolean(3, esActiva); // Ya estaba correcto
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int newId = rs.getInt(1);
                    System.out.println("✅ Cuenta bancaria creada con ID: " + newId);
                    return get(newId);
                }
            }
        }
        throw new SQLException("Error al insertar NroCuenta.");
    }

    public List<String[]> update(int id, String banco, int nroCuenta, boolean esActiva) throws SQLException {
        System.out.println("🔧 Actualizando cuenta bancaria ID " + id + ":");
        System.out.println("   Banco: " + banco);
        System.out.println("   Número cuenta: " + nroCuenta);
        System.out.println("   Es activa: " + esActiva);

        String query = "UPDATE nro_cuentas SET banco = ?, nro_cuenta = ?, es_activa = ? WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, banco);
            ps.setInt(2, nroCuenta);
            ps.setBoolean(3, esActiva); // Ya estaba correcto
            ps.setInt(4, id);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Error al actualizar NroCuenta.");
            }
            System.out.println("✅ Cuenta bancaria ID " + id + " actualizada");
        }
        return get(id);
    }

    public List<String[]> delete(int id) throws SQLException {
        String query = "DELETE FROM nro_cuentas WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Error al eliminar NroCuenta.");
            }
            System.out.println("🗑️ Cuenta bancaria ID " + id + " eliminada");
        }
        return list();
    }

    public List<String[]> list() throws SQLException {
        List<String[]> lista = new ArrayList<>();
        String query = "SELECT * FROM nro_cuentas";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("banco"),
                        String.valueOf(rs.getInt("nro_cuenta")),
                        String.valueOf(rs.getBoolean("es_activa")) // Ya estaba correcto
                });
            }
        }
        return lista;
    }

    public void disconnect() {
        connection.closeConnection();
    }
}