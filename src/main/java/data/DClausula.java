// File: data/DClausula.java
package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

public class DClausula {
    public static final String[] HEADERS = {"id", "descripcion", "activa"};

    private final SqlConnection connection;

    public DClausula() {
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
        String query = "SELECT * FROM \"clausulas\" WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    resultado.add(new String[]{
                            String.valueOf(rs.getInt("id")),
                            rs.getString("descripcion"),
                            String.valueOf(rs.getBoolean("activa")) // Convertir boolean a string
                    });
                }
            }
        }
        return resultado;
    }

    public List<String[]> save(String descripcion, String activa) throws SQLException {
        // CORRECCIÓN: Convertir string a boolean
        boolean activaBoolean = parseBoolean(activa);

        System.out.println("🔧 Creando cláusula:");
        System.out.println("   Descripción: " + descripcion);
        System.out.println("   Activa (string): " + activa);
        System.out.println("   Activa (boolean): " + activaBoolean);

        String query = "INSERT INTO \"clausulas\" (descripcion, activa) VALUES (?, ?) RETURNING id";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, descripcion);
            ps.setBoolean(2, activaBoolean); // Usar setBoolean en lugar de setString
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int newId = rs.getInt(1);
                    System.out.println("✅ Cláusula creada con ID: " + newId);
                    return get(newId);
                }
            }
        }
        throw new SQLException("Error al insertar clausulas.");
    }

    public List<String[]> update(int id, String descripcion, String activa) throws SQLException {
        // CORRECCIÓN: Convertir string a boolean
        boolean activaBoolean = parseBoolean(activa);

        System.out.println("🔧 Actualizando cláusula ID " + id + ":");
        System.out.println("   Descripción: " + descripcion);
        System.out.println("   Activa (string): " + activa);
        System.out.println("   Activa (boolean): " + activaBoolean);

        String query = "UPDATE \"clausulas\" SET descripcion = ?, activa = ? WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, descripcion);
            ps.setBoolean(2, activaBoolean); // Usar setBoolean en lugar de setString
            ps.setInt(3, id);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Error al actualizar clausulas.");
            }
            System.out.println("✅ Cláusula ID " + id + " actualizada");
        }
        return get(id);
    }

    public List<String[]> delete(int id) throws SQLException {
        String query = "DELETE FROM \"clausulas\" WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Error al eliminar clausulas.");
            }
            System.out.println("🗑️ Cláusula ID " + id + " eliminada");
        }
        return list();
    }

    public List<String[]> list() throws SQLException {
        List<String[]> lista = new ArrayList<>();
        String query = "SELECT * FROM \"clausulas\"";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("descripcion"),
                        String.valueOf(rs.getBoolean("activa")) // Convertir boolean a string
                });
            }
        }
        return lista;
    }

    public void disconnect() {
        connection.closeConnection();
    }
}