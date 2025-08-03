package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;
import librerias.PasswordHasher;

/**
 * Data‑access layer for the <code>users</code> table.
 * <p>
 *    Características principales:
 *    <ul>
 *        <li><code>email</code></li>
 *        <li><code>password</code> - Se hashea automáticamente con Bcrypt compatible con Laravel</li>
 *        <li><code>documento_frontal_path</code></li>
 *        <li><code>documento_trasero_path</code></li>
 *        <li><code>verificado</code> - Estado de verificación del usuario</li>
 *    </ul>
 * </p>
 */
public class DUser {

    /**
     * Headers usados en los comandos de email - ACTUALIZADO con 'verificado'.
     */
    public static final String[] HEADERS = {
            "id", "apellido", "ci", "domicilio", "name", "telefono",
            "email", "password", "documento_frontal_path", "documento_trasero_path", "verificado"
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
     * Mapea una fila del ResultSet a un String[] en el orden definido en HEADERS.
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
                "[PROTECTED]", // No mostrar la contraseña hasheada por seguridad
                rs.getString("documento_frontal_path"),
                rs.getString("documento_trasero_path"),
                rs.getString("verificado") // ⭐ NUEVO CAMPO
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

    /**
     * Crea un nuevo usuario con contraseña hasheada automáticamente.
     * ⭐ AHORA INCLUYE EL CAMPO 'verificado'
     */
    public List<String[]> save(String apellido,
                               int ci,
                               String domicilio,
                               String name,
                               int telefono,
                               String email,
                               String password,
                               String documentoFrontalPath,
                               String documentoTraseroPath,
                               String verificado) throws SQLException {

        // PASO CRÍTICO: Hashear la contraseña antes de guardarla
        String hashedPassword;

        if (PasswordHasher.isBcryptHash(password)) {
            // La contraseña ya está hasheada
            hashedPassword = password;
            System.out.println("🔓 Contraseña ya hasheada para: " + email);
        } else {
            // Hashear la contraseña en texto plano
            hashedPassword = PasswordHasher.hashPassword(password);
            System.out.println("🔐 Hasheando nueva contraseña para: " + email);
            System.out.println("   Contraseña original: " + password);
            System.out.println("   Hash Laravel-compatible: " + hashedPassword);
        }

        String query = "INSERT INTO \"users\" (apellido, ci, domicilio, name, telefono, email, password, documento_frontal_path, documento_trasero_path, verificado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, apellido);
            ps.setInt(2, ci);
            ps.setString(3, domicilio);
            ps.setString(4, name);
            ps.setInt(5, telefono);
            ps.setString(6, email);
            ps.setString(7, hashedPassword); // ⚡ CONTRASEÑA HASHEADA
            ps.setString(8, documentoFrontalPath);
            ps.setString(9, documentoTraseroPath);
            ps.setString(10, verificado); // ⭐ NUEVO CAMPO

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int newId = rs.getInt(1);
                    System.out.println("✅ Usuario creado exitosamente:");
                    System.out.println("   ID: " + newId);
                    System.out.println("   Email: " + email);
                    System.out.println("   Verificado: " + verificado);
                    System.out.println("   Hash compatible con Laravel: SÍ");
                    return get(newId);
                }
            }
        }
        throw new SQLException("Error al insertar usuario.");
    }

    /**
     * Actualiza un usuario, hasheando la contraseña si es necesario.
     * ⭐ AHORA INCLUYE EL CAMPO 'verificado'
     */
    public List<String[]> update(int id,
                                 String apellido,
                                 int ci,
                                 String domicilio,
                                 String name,
                                 int telefono,
                                 String email,
                                 String password,
                                 String documentoFrontalPath,
                                 String documentoTraseroPath,
                                 String verificado) throws SQLException {

        // Verificar si la contraseña necesita ser hasheada
        String finalPassword;

        if (PasswordHasher.isBcryptHash(password)) {
            finalPassword = password;
            System.out.println("📝 Manteniendo contraseña ya hasheada para usuario ID: " + id);
        } else {
            finalPassword = PasswordHasher.hashPassword(password);
            System.out.println("🔐 Hasheando nueva contraseña para usuario ID: " + id);
        }

        String query = "UPDATE \"users\" SET apellido = ?, ci = ?, domicilio = ?, name = ?, telefono = ?, email = ?, password = ?, documento_frontal_path = ?, documento_trasero_path = ?, verificado = ? " +
                "WHERE id = ?";

        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, apellido);
            ps.setInt(2, ci);
            ps.setString(3, domicilio);
            ps.setString(4, name);
            ps.setInt(5, telefono);
            ps.setString(6, email);
            ps.setString(7, finalPassword);
            ps.setString(8, documentoFrontalPath);
            ps.setString(9, documentoTraseroPath);
            ps.setString(10, verificado); // ⭐ NUEVO CAMPO
            ps.setInt(11, id);

            if (ps.executeUpdate() == 0) {
                throw new SQLException("Error al actualizar usuario.");
            }

            System.out.println("✅ Usuario ID " + id + " actualizado:");
            System.out.println("   Email: " + email);
            System.out.println("   Verificado: " + verificado);
            System.out.println("   Contraseña hasheada: SÍ");
        }
        return get(id);
    }

    public List<String[]> delete(int id) throws SQLException {
        String query = "DELETE FROM \"users\" WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Error al eliminar usuario.");
            }
            System.out.println("🗑️ Usuario ID " + id + " eliminado");
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

    /**
     * Método auxiliar para verificar credenciales de login.
     * Útil para testing y validaciones internas.
     */
    public boolean verifyCredentials(String email, String password) throws SQLException {
        String query = "SELECT password FROM \"users\" WHERE email = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("password");
                    boolean isValid = PasswordHasher.verifyPassword(password, hashedPassword);
                    System.out.println("🔍 Verificación de credenciales para: " + email + " → " +
                            (isValid ? "VÁLIDA" : "INVÁLIDA"));
                    return isValid;
                }
            }
        }
        System.out.println("❌ Usuario no encontrado: " + email);
        return false;
    }

    /**
     * ⭐ NUEVO MÉTODO: Cambiar solo el estado de verificación de un usuario
     */
    public List<String[]> updateVerificationStatus(int id, String nuevoEstado) throws SQLException {
        String query = "UPDATE \"users\" SET verificado = ? WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, id);

            if (ps.executeUpdate() == 0) {
                throw new SQLException("Usuario no encontrado para actualizar verificación.");
            }

            System.out.println("✅ Estado de verificación actualizado:");
            System.out.println("   Usuario ID: " + id);
            System.out.println("   Nuevo estado: " + nuevoEstado);
        }
        return get(id);
    }

    public void disconnect() {
        connection.closeConnection();
    }
}