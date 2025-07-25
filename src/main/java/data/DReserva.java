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
        String sql = "SELECT * FROM \"reservas\" WHERE id = ?";
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
     * Crea una reserva, actualiza el estado del vehículo a "reservado"
     * y automáticamente genera un pago asociado.
     */
    public List<String[]> save(String estado, int vehiculoId, int userId) throws SQLException {
        try (Connection conn = connection.connect()) {
            conn.setAutoCommit(false); // Iniciar transacción

            try {
                // 1. Verificar que el vehículo existe y obtener su precio
                float precioDia = 0f;
                String sqlVehiculo = "SELECT precio_dia, estado FROM \"vehiculos\" WHERE id = ?";
                try (PreparedStatement psVehiculo = conn.prepareStatement(sqlVehiculo)) {
                    psVehiculo.setInt(1, vehiculoId);
                    try (ResultSet rs = psVehiculo.executeQuery()) {
                        if (rs.next()) {
                            precioDia = rs.getFloat("precio_dia");
                            String estadoVehiculo = rs.getString("estado");

                            // Opcional: verificar si el vehículo ya está reservado
                            if ("reservado".equalsIgnoreCase(estadoVehiculo)) {
                                throw new SQLException("El vehículo con ID " + vehiculoId + " ya está reservado");
                            }
                        } else {
                            throw new SQLException("Vehículo no encontrado: " + vehiculoId);
                        }
                    }
                }

                // 2. Actualizar el estado del vehículo a "reservado"
                String sqlUpdateVehiculo = "UPDATE \"vehiculos\" SET estado = 'reservado' WHERE id = ?";
                try (PreparedStatement psUpdateVehiculo = conn.prepareStatement(sqlUpdateVehiculo)) {
                    psUpdateVehiculo.setInt(1, vehiculoId);
                    int rowsUpdated = psUpdateVehiculo.executeUpdate();
                    if (rowsUpdated == 0) {
                        throw new SQLException("No se pudo actualizar el estado del vehículo: " + vehiculoId);
                    }
                    System.out.println("✅ Vehículo ID " + vehiculoId + " marcado como reservado");
                }

                // 3. Crear la reserva
                String sqlReserva = "INSERT INTO \"reservas\" " +
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

                // 4. Crear pago automático (por defecto 1 día de renta)
                LocalDate hoy = LocalDate.now();
                Date fechaDesde = Date.valueOf(hoy);
                Date fechaHasta = Date.valueOf(hoy.plusDays(1));
                Date fechaPago = Date.valueOf(hoy);

                String sqlPago = "INSERT INTO \"pagos\" " +
                        "(desde, fecha, hasta, estado, pagofacil_transaction_id, monto, tipo_pago, reserva_id) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement psPago = conn.prepareStatement(sqlPago)) {
                    psPago.setDate(1, fechaDesde);
                    psPago.setDate(2, fechaPago);
                    psPago.setDate(3, fechaHasta);
                    psPago.setString(4, "PENDIENTE");
                    psPago.setString(5, null); // pagofacil_transaction_id
                    psPago.setFloat(6, precioDia);
                    psPago.setString(7, "reserva");
                    psPago.setInt(8, reservaId);

                    psPago.executeUpdate();
                    System.out.println("✅ Pago automático creado para la reserva ID " + reservaId);
                }

                // 5. Commit de la transacción
                conn.commit();
                System.out.println("✅ Reserva creada exitosamente con ID " + reservaId);

                // 6. Retornar la reserva creada
                return get(reservaId);

            } catch (SQLException e) {
                conn.rollback(); // Rollback en caso de error
                System.err.println("❌ Error al crear reserva, realizando rollback: " + e.getMessage());
                throw e;
            } finally {
                conn.setAutoCommit(true); // Restaurar auto-commit
            }
        }
    }

    /**
     * Actualiza una reserva. Si se cancela, libera el vehículo.
     */
    public List<String[]> update(int id, String estado, int vehiculoId, int userId) throws SQLException {
        try (Connection conn = connection.connect()) {
            conn.setAutoCommit(false);

            try {
                // Si el nuevo estado es "cancelado", liberar el vehículo
                if ("cancelado".equalsIgnoreCase(estado)) {
                    String sqlUpdateVehiculo = "UPDATE \"vehiculos\" SET estado = 'disponible' WHERE id = ?";
                    try (PreparedStatement psUpdateVehiculo = conn.prepareStatement(sqlUpdateVehiculo)) {
                        psUpdateVehiculo.setInt(1, vehiculoId);
                        psUpdateVehiculo.executeUpdate();
                        System.out.println("✅ Vehículo ID " + vehiculoId + " liberado (estado: disponible)");
                    }
                }

                // Actualizar la reserva
                String sql = "UPDATE \"reservas\" SET " +
                        "estado = ?, vehiculo_id = ?, user_id = ? " +
                        "WHERE id = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, estado);
                    ps.setInt(2, vehiculoId);
                    ps.setInt(3, userId);
                    ps.setInt(4, id);
                    if (ps.executeUpdate() == 0) {
                        throw new SQLException("Error al actualizar reserva.");
                    }
                }

                conn.commit();
                return get(id);

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    /**
     * Elimina una reserva y libera el vehículo.
     */
    public List<String[]> delete(int id) throws SQLException {
        try (Connection conn = connection.connect()) {
            conn.setAutoCommit(false);

            try {
                // Primero obtener el vehiculo_id de la reserva que se va a eliminar
                int vehiculoId = -1;
                String sqlGetVehiculo = "SELECT vehiculo_id FROM \"reservas\" WHERE id = ?";
                try (PreparedStatement psGet = conn.prepareStatement(sqlGetVehiculo)) {
                    psGet.setInt(1, id);
                    try (ResultSet rs = psGet.executeQuery()) {
                        if (rs.next()) {
                            vehiculoId = rs.getInt("vehiculo_id");
                        }
                    }
                }

                // Eliminar la reserva
                String sql = "DELETE FROM \"reservas\" WHERE id = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, id);
                    if (ps.executeUpdate() == 0) {
                        throw new SQLException("Error al eliminar reserva.");
                    }
                }

                // Liberar el vehículo si se encontró
                if (vehiculoId != -1) {
                    String sqlUpdateVehiculo = "UPDATE \"vehiculos\" SET estado = 'disponible' WHERE id = ?";
                    try (PreparedStatement psUpdateVehiculo = conn.prepareStatement(sqlUpdateVehiculo)) {
                        psUpdateVehiculo.setInt(1, vehiculoId);
                        psUpdateVehiculo.executeUpdate();
                        System.out.println("✅ Vehículo ID " + vehiculoId + " liberado tras eliminar reserva");
                    }
                }

                conn.commit();
                return list();

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public List<String[]> list() throws SQLException {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT * FROM \"reservas\"";
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