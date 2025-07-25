package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

/**
 * Data‑access layer for the <code>users</code> table.
 * <p>
 *    Now supports the new columns:
 *    <ul>
 *        <li><code>email</code></li>
 *        <li><code>password</code></li>
 *        <li><code>documento_frontal_path</code></li>
 *        <li><code>documento_trasero_path</code></li>
 *    </ul>
 * </p>
 */
public class DUser {

    /**
     * Header order used by the e‑mail commands.
     */
    public static final String[] HEADERS = {
            "id", "apellido", "ci", "domicilio", "name", "telefono",
            "email", "password", "documento_frontal_path", "documento_trasero_path"
    };

    private final SqlConnection connection;

    public DUser() {
        this.connection = new SqlConnection(
                DBConnection.database,
                DBConnection.server,
                DBConnection.port,
                DBConnection.user,
                DBConnection.password
        );
    }

    /* ─────────────────────────────  Helpers  ───────────────────────────── */

    /**
     * Maps the current row of a {@link ResultSet} to a <code>String[]</code> in
     * the exact order defined in {@link #HEADERS}.
     */
    private String[] mapRow(ResultSet rs) throws SQLException {
        return new String[]{
                String.valueOf(rs.getInt("id")),
                rs.getString("apellido"),
                String.valueOf(rs.getInt("ci")),
                rs.getString("domicilio"),
                rs.getString("name"),
                String.valueOf(rs.getInt("telefono")),
                rs.getString("email"),
                rs.getString("password"),
                rs.getString("documento_frontal_path"),
                rs.getString("documento_trasero_path")
        };
    }

    /* ─────────────────────────  CRUD operations  ───────────────────────── */

    public List<String[]> get(int id) throws SQLException {
        List<String[]> resultado = new ArrayList<>();
        String query = "SELECT * FROM \"users\" WHERE id = ?";
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

    public List<String[]> save(String apellido,
                               int ci,
                               String domicilio,
                               String name,
                               int telefono,
                               String email,
                               String password,
                               String documentoFrontalPath,
                               String documentoTraseroPath) throws SQLException {
        String query = "INSERT INTO \"users\" (apellido, ci, domicilio, name, telefono, email, password, documento_frontal_path, documento_trasero_path) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, apellido);
            ps.setInt(2, ci);
            ps.setString(3, domicilio);
            ps.setString(4, name);
            ps.setInt(5, telefono);
            ps.setString(6, email);
            ps.setString(7, password);
            ps.setString(8, documentoFrontalPath);
            ps.setString(9, documentoTraseroPath);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int newId = rs.getInt(1);
                    return get(newId);
                }
            }
        }
        throw new SQLException("Error al insertar users.");
    }

    public List<String[]> update(int id,
                                 String apellido,
                                 int ci,
                                 String domicilio,
                                 String name,
                                 int telefono,
                                 String email,
                                 String password,
                                 String documentoFrontalPath,
                                 String documentoTraseroPath) throws SQLException {
        String query = "UPDATE \"users\" SET apellido = ?, ci = ?, domicilio = ?, name = ?, telefono = ?, email = ?, password = ?, documento_frontal_path = ?, documento_trasero_path = ? " +
                "WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, apellido);
            ps.setInt(2, ci);
            ps.setString(3, domicilio);
            ps.setString(4, name);
            ps.setInt(5, telefono);
            ps.setString(6, email);
            ps.setString(7, password);
            ps.setString(8, documentoFrontalPath);
            ps.setString(9, documentoTraseroPath);
            ps.setInt(10, id);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Error al actualizar users.");
            }
        }
        return get(id);
    }

    public List<String[]> delete(int id) throws SQLException {
        String query = "DELETE FROM \"users\" WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Error al eliminar users.");
            }
        }
        return list();
    }

    public List<String[]> list() throws SQLException {
        List<String[]> lista = new ArrayList<>();
        String query = "SELECT * FROM \"users\"";
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
