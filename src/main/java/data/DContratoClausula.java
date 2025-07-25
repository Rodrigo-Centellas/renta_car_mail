// File: data/DContratoClausula.java
package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

public class DContratoClausula {

    public static final String[] HEADERS = {
            "id", "contrato_id", "clausula_id"
    };

    private final SqlConnection connection;

    public DContratoClausula() {
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
        String sql = "SELECT * FROM \"contrato_clausulas\" WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result.add(new String[]{
                            String.valueOf(rs.getInt("id")),
                            String.valueOf(rs.getInt("contrato_id")),
                            String.valueOf(rs.getInt("clausula_id"))
                    });
                }
            }
        }
        return result;
    }

    public List<String[]> save(int contratoId, int clausulaId) throws SQLException {
        String sql = "INSERT INTO \"contrato_clausulas\" (contrato_id, clausula_id) "
                + "VALUES (?, ?) RETURNING id";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, contratoId);
            ps.setInt(2, clausulaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int newId = rs.getInt(1);
                    return get(newId);
                }
            }
        }
        throw new SQLException("Error al insertar contrato_clausulas.");
    }

    public List<String[]> update(int id, int contratoId, int clausulaId) throws SQLException {
        String sql = "UPDATE \"contrato_clausulas\" SET contrato_id = ?, clausula_id = ? WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, contratoId);
            ps.setInt(2, clausulaId);
            ps.setInt(3, id);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Error al actualizar contrato_clausulas.");
            }
        }
        return get(id);
    }

    public List<String[]> delete(int id) throws SQLException {
        // List<String[]> lista = list();
        String sql = "DELETE FROM \"contrato_clausulas\" WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Error al eliminar contrato_clausulas.");
            }
        }
        return list();
    }

    public List<String[]> list() throws SQLException {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT * FROM \"contrato_clausulas\"";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("contrato_id")),
                        String.valueOf(rs.getInt("clausula_id"))
                });
            }
        }
        return list;
    }

    public void disconnect() {
        connection.closeConnection();
    }
}
