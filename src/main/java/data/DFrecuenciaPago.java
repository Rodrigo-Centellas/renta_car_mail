// File: data/DFrecuenciaPago.java
package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

public class DFrecuenciaPago {

    public static final String[] HEADERS = {"id", "frecuencia_dias", "nombre"};

    private final SqlConnection connection;

    public DFrecuenciaPago() {
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
        String query = "SELECT * FROM \"frecuencia_pagos\" WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    resultado.add(new String[]{
                            String.valueOf(rs.getInt("id")),
                            String.valueOf(rs.getInt("frecuencia_dias")),
                            rs.getString("nombre")
                    });
                }
            }
        }
        return resultado;
    }

    public List<String[]> save(int frecuenciaDias, String nombre) throws SQLException {
        String query = "INSERT INTO \"frecuencia_pagos\" (frecuencia_dias, nombre) VALUES (?, ?) RETURNING id";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, frecuenciaDias);
            ps.setString(2, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int newId = rs.getInt(1);
                    return get(newId);
                }
            }
        }
        throw new SQLException("Error al insertar frecuencia_pagos.");
    }

    public List<String[]> update(int id, int frecuenciaDias, String nombre) throws SQLException {
        String query = "UPDATE \"frecuencia_pagos\" SET frecuencia_dias = ?, nombre = ? WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, frecuenciaDias);
            ps.setString(2, nombre);
            ps.setInt(3, id);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Error al actualizar frecuencia_pagos.");
            }
        }
        return get(id);
    }

    public List<String[]> delete(int id) throws SQLException {
        //List<String[]> lista = list();
        String query = "DELETE FROM \"frecuencia_pagos\" WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Error al eliminar frecuencia_pagos.");
            }
        }
        return list();
    }

    public List<String[]> list() throws SQLException {
        List<String[]> lista = new ArrayList<>();
        String query = "SELECT * FROM \"frecuencia_pagos\"";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("frecuencia_dias")),
                        rs.getString("nombre")
                });
            }
        }
        return lista;
    }

    public void disconnect() {
        connection.closeConnection();
    }
}
