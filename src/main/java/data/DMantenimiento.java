package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

public class DMantenimiento {

    public static final String[] HEADERS = {"id", "descripcion", "nombre"};

    private final SqlConnection connection;

    public DMantenimiento() {
        this.connection = new SqlConnection(
                DBConnection.database,
                DBConnection.server,
                DBConnection.port,
                DBConnection.user,
                DBConnection.password
        );
    }

    public List<String[]> get(int id) throws SQLException {
        List<String[]> mantenimientos = new ArrayList<>();
        String query = "SELECT * FROM \"mantenimientos\" WHERE id = ?";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            mantenimientos.add(new String[]{
                    String.valueOf(rs.getInt("id")),
                    rs.getString("descripcion"),
                    rs.getString("nombre")
            });
        }
        return mantenimientos;
    }

    public List<String[]> save(String descripcion, String nombre) throws SQLException {
        String query = "INSERT INTO \"mantenimientos\" (descripcion, nombre) VALUES (?, ?) RETURNING id";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setString(1, descripcion);
        ps.setString(2, nombre);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int newId = rs.getInt(1);
            return get(newId);
        } else {
            throw new SQLException("Error al insertar mantenimientos. No se pudo recuperar el ID del mantenimientos.");
        }
    }

    public List<String[]> update(int id, String descripcion, String nombre) throws SQLException {
        String query = "UPDATE \"mantenimientos\" SET descripcion = ?, nombre = ? WHERE id = ?";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setString(1, descripcion);
        ps.setString(2, nombre);
        ps.setInt(3, id);
        if (ps.executeUpdate() == 0) {
            System.err.println("Error al modificar el mantenimientos");
            throw new SQLException();
        }
        return get(id);
    }

    public List<String[]> delete(int id) throws SQLException {
        //List<String[]> mantenimientos = list();
        String query = "DELETE FROM \"mantenimientos\" WHERE id = ?";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setInt(1, id);
        if (ps.executeUpdate() == 0) {
            System.err.println("Error al eliminar mantenimientos");
            throw new SQLException();
        }
        return list();
    }

    public List<String[]> list() throws SQLException {
        List<String[]> mantenimientos = new ArrayList<>();
        String query = "SELECT * FROM \"mantenimientos\"";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                mantenimientos.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("descripcion"),
                        rs.getString("nombre")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error de conexión al intentar listar mantenimientos.");
            throw e;
        }
        return mantenimientos;
    }

    public void disconnect() {
        connection.closeConnection();
    }
}
