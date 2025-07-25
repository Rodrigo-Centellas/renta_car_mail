// File: data/DReserva.java
package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

public class DReserva {

    public static final String[] HEADERS = {
            "id", "estado", "fecha", "vehiculo_id", "user_id"
    };

    private final SqlConnection connection;

    public DReserva() {
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
        String sql = "SELECT * FROM \"Reserva\" WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Timestamp ts = rs.getTimestamp("fecha");
                    result.add(new String[]{
                            String.valueOf(rs.getInt("id")),
                            rs.getString("estado"),
                            ts != null ? ts.toString() : null,
                            String.valueOf(rs.getInt("vehiculo_id")),
                            String.valueOf(rs.getInt("user_id"))
                    });
                }
            }
        }
        return result;
    }

    /**
     * Crea una reserva y automáticamente genera un pago asociado.
     */
    public List<String[]> save(String estado, int vehiculoId, int userId) throws SQLException {
        try (Connection conn = connection.connect()) {
            conn.setAutoCommit(false); // Iniciar transacción

            try {
                // 1. Crear la reserva
                String sqlReserva = "INSERT INTO \"Reserva\" " +
                        "(estado, fecha, vehiculo_id, user_id) " +
                        "VALUES (?, CURRENT_TIMESTAMP, ?, ?) RETURNING id";

                int reservaId;
                try (PreparedStatement psReserva = conn.prepareStatement(sqlReserva)) {
                    psReserva.setString(1, estado);
                    psReserva.setInt(2, vehiculoId);
                    psReserva.setInt(3, userId);

                    try (ResultSet rs = psReserva.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("No se pudo crear la reserva");
                        }
                        reservaId = rs.getInt(1);
                    }
                }

                // 2. Obtener precio del vehículo para calcular monto
                float precioDia = 0f;
                String sqlVehiculo = "SELECT precio_dia FROM \"Vehiculo\" WHERE id = ?";
                try (PreparedStatement psVehiculo = conn.prepareStatement(sqlVehiculo)) {
                    psVehiculo.setInt(1, vehiculoId);
                    try (ResultSet rs = psVehiculo.executeQuery()) {
                        if (rs.next()) {
                            precioDia = rs.getFloat("precio_dia");
                        } else {
                            throw new SQLException("Vehículo no encontrado: " + vehiculoId);
                        }
                    }
                }

                // 3. Crear pago automático (por defecto 1 día de renta)
                LocalDate hoy = LocalDate.now();
                Date fechaDesde = Date.valueOf(hoy);
                Date fechaHasta = Date.valueOf(hoy.plusDays(1));
                Date fechaPago = Date.valueOf(hoy);

                String sqlPago = "INSERT INTO \"Pago\" " +
                        "(desde, fecha, hasta, estado, monto, tipo_pago, pagofacil_transaction_id, reserva_id) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement psPago = conn.prepareStatement(sqlPago)) {
                    psPago.setDate(1, fechaDesde);
                    psPago.setDate(2, fechaPago);
                    psPago.setDate(3, fechaHasta);
                    psPago.setString(4, "PENDIENTE");
                    psPago.setFloat(5, precioDia); // Monto = precio por día
                    psPago.setString(6, "PENDIENTE");
                    psPago.setString(7, null); // Sin transaction ID inicial
                    psPago.setInt(8, reservaId);

                    psPago.executeUpdate();
                }

                // 4. Commit de la transacción
                conn.commit();

                // 5. Retornar la reserva creada
                return get(reservaId);

            } catch (SQLException e) {
                conn.rollback(); // Rollback en caso de error
                throw e;
            } finally {
                conn.setAutoCommit(true); // Restaurar auto-commit
            }
        }
    }

    /**
     * No tocamos la fecha al actualizar.
     */
    public List<String[]> update(int id, String estado, int vehiculoId, int userId) throws SQLException {
        String sql = "UPDATE \"Reserva\" SET " +
                "estado = ?, vehiculo_id = ?, user_id = ? " +
                "WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, vehiculoId);
            ps.setInt(3, userId);
            ps.setInt(4, id);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Error al actualizar Reserva.");
            }
        }
        return get(id);
    }

    public List<String[]> delete(int id) throws SQLException {
        String sql = "DELETE FROM \"Reserva\" WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Error al eliminar Reserva.");
            }
        }
        return list();
    }

    public List<String[]> list() throws SQLException {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT * FROM \"Reserva\"";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("fecha");
                list.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("estado"),
                        ts != null ? ts.toString() : null,
                        String.valueOf(rs.getInt("vehiculo_id")),
                        String.valueOf(rs.getInt("user_id"))
                });
            }
        }
        return list;
    }

    public void disconnect() {
        connection.closeConnection();
    }
}