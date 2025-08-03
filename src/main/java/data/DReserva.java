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
     * Parámetros: estado, vehiculo_id, user_id, fecha
     */
    public List<String[]> save(String estado, int vehiculoId, int userId, Date fecha) throws SQLException {
        Connection conn = null;
        try {
            conn = connection.connect();
            conn.setAutoCommit(false); // Iniciar transacción

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
                    "VALUES (?, ?, ?, ?) RETURNING id";

            int reservaId;
            try (PreparedStatement psReserva = conn.prepareStatement(sqlReserva)) {
                psReserva.setString(1, estado);
                psReserva.setDate(2, fecha);
                psReserva.setInt(3, vehiculoId);
                psReserva.setInt(4, userId);

                try (ResultSet rs = psReserva.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("No se pudo crear la reserva");
                    }
                    reservaId = rs.getInt(1);
                }
            }

            // 4. Crear pago automático calculando días desde hoy hasta fecha reserva
            LocalDate hoy = LocalDate.now();
            LocalDate fechaReserva = fecha.toLocalDate();

            // Calcular días de diferencia (fecha reserva - hoy)
            long diasDiferencia = java.time.temporal.ChronoUnit.DAYS.between(hoy, fechaReserva);

            // Si la reserva es para el pasado o hoy, mínimo 1 día
            if (diasDiferencia <= 0) {
                diasDiferencia = 1;
            }

            // Calcular monto total
            float montoTotal = precioDia * diasDiferencia;

            Date fechaDesde = Date.valueOf(hoy);
            Date fechaHasta = Date.valueOf(fechaReserva);
            Date fechaPago = Date.valueOf(hoy);

            System.out.println("💰 Cálculo de pago:");
            System.out.println("   - Fecha hoy: " + hoy);
            System.out.println("   - Fecha reserva: " + fechaReserva);
            System.out.println("   - Días de diferencia: " + diasDiferencia);
            System.out.println("   - Precio por día: $" + precioDia);
            System.out.println("   - Monto total: $" + montoTotal);

            String sqlPago = "INSERT INTO \"pagos\" " +
                    "(desde, fecha, hasta, estado, pagofacil_transaction_id, monto, tipo_pago, reserva_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement psPago = conn.prepareStatement(sqlPago)) {
                psPago.setDate(1, fechaDesde);
                psPago.setDate(2, fechaPago);
                psPago.setDate(3, fechaHasta);
                psPago.setString(4, "pendiente");
                psPago.setString(5, null); // pagofacil_transaction_id
                psPago.setFloat(6, montoTotal);
                psPago.setString(7, "reserva");
                psPago.setInt(8, reservaId);

                psPago.executeUpdate();
                System.out.println("✅ Pago automático creado para la reserva ID " + reservaId + " por $" + montoTotal);
            }

            // 5. Commit de la transacción
            conn.commit();
            System.out.println("✅ Reserva creada exitosamente con ID " + reservaId);

            // 6. Obtener la reserva creada para retornar
            List<String[]> result = new ArrayList<>();
            String sqlGet = "SELECT * FROM \"reservas\" WHERE id = ?";
            try (PreparedStatement psGet = conn.prepareStatement(sqlGet)) {
                psGet.setInt(1, reservaId);
                try (ResultSet rs = psGet.executeQuery()) {
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

        } catch (SQLException e) {
            // Rollback en caso de error
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("❌ Error en rollback: " + rollbackEx.getMessage());
                }
            }
            System.err.println("❌ Error al crear reserva: " + e.getMessage());
            throw e;
        } finally {
            // Restaurar auto-commit y NO cerrar la conexión aquí
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ex) {
                    System.err.println("❌ Error restaurando autocommit: " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Actualiza solo el estado de una reserva. Si se cancela, libera el vehículo.
     * Parámetros: id, estado
     */
    public List<String[]> update(int id, String estado) throws SQLException {
        Connection conn = null;
        try {
            conn = connection.connect();
            conn.setAutoCommit(false);

            // Obtener el vehiculo_id de la reserva antes de actualizar
            int vehiculoId = -1;
            String sqlGetVehiculo = "SELECT vehiculo_id FROM \"reservas\" WHERE id = ?";
            try (PreparedStatement psGet = conn.prepareStatement(sqlGetVehiculo)) {
                psGet.setInt(1, id);
                try (ResultSet rs = psGet.executeQuery()) {
                    if (rs.next()) {
                        vehiculoId = rs.getInt("vehiculo_id");
                    } else {
                        throw new SQLException("Reserva no encontrada: " + id);
                    }
                }
            }

            // Si el nuevo estado es "cancelado", liberar el vehículo
            if ("cancelado".equalsIgnoreCase(estado)) {
                String sqlUpdateVehiculo = "UPDATE \"vehiculos\" SET estado = 'Disponible' WHERE id = ?";
                try (PreparedStatement psUpdateVehiculo = conn.prepareStatement(sqlUpdateVehiculo)) {
                    psUpdateVehiculo.setInt(1, vehiculoId);
                    psUpdateVehiculo.executeUpdate();
                    System.out.println("✅ Vehículo ID " + vehiculoId + " liberado (estado: disponible)");
                }
            }

            // Actualizar solo el estado de la reserva
            String sql = "UPDATE \"reservas\" SET estado = ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, estado);
                ps.setInt(2, id);
                if (ps.executeUpdate() == 0) {
                    throw new SQLException("Error al actualizar reserva.");
                }
            }

            conn.commit();

            // Obtener el resultado actualizado
            return getReservaById(id, conn);

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("❌ Error en rollback: " + rollbackEx.getMessage());
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ex) {
                    System.err.println("❌ Error restaurando autocommit: " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Elimina una reserva y libera el vehículo.
     */
    public List<String[]> delete(int id) throws SQLException {
        Connection conn = null;
        try {
            conn = connection.connect();
            conn.setAutoCommit(false);

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
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("❌ Error en rollback: " + rollbackEx.getMessage());
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ex) {
                    System.err.println("❌ Error restaurando autocommit: " + ex.getMessage());
                }
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

    /**
     * Método helper para obtener una reserva por ID usando una conexión existente
     */
    private List<String[]> getReservaById(int id, Connection conn) throws SQLException {
        List<String[]> result = new ArrayList<>();
        String sql = "SELECT * FROM \"reservas\" WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
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

    public void disconnect() {
        connection.closeConnection();
    }
}