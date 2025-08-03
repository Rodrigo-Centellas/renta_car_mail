// File: data/DContrato.java
package data;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

public class DContrato {

    public static final String[] HEADERS = {
            "Resultado",
            "ID_Contrato",
            "Estado_Vehiculo",
            "Periodo_Alquiler",
            "Pagos_Generados",
            "Total_a_Pagar"
    };

    /**
     * Método helper para obtener datos del contrato usando una conexión existente
     */
    private List<String[]> getContratoData(int id, Connection conn) throws SQLException {
        List<String[]> result = new ArrayList<>();
        String sql = "SELECT * FROM \"contratos\" WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result.add(new String[]{
                            String.valueOf(rs.getInt("id")),
                            rs.getString("estado"),
                            rs.getDate("fecha_inicio").toString(),
                            rs.getDate("fecha_fin").toString(),
                            String.valueOf(rs.getInt("frecuencia_pago_id")),
                            String.valueOf(rs.getInt("nro_cuenta_id")),
                            String.valueOf(rs.getInt("vehiculo_id"))
                    });
                }
            }
        }
        return result;
    };

    private final SqlConnection connection;

    public DContrato() {
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
        String sql = "SELECT * FROM \"contratos\" WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result.add(new String[]{
                            String.valueOf(rs.getInt("id")),
                            rs.getString("estado"),
                            rs.getDate("fecha_inicio").toString(),
                            rs.getDate("fecha_fin").toString(),
                            String.valueOf(rs.getInt("frecuencia_pago_id")),
                            String.valueOf(rs.getInt("nro_cuenta_id")),
                            String.valueOf(rs.getInt("vehiculo_id"))
                    });
                }
            }
        }
        return result;
    }

    public static final String[] HEADERS_GET = {
            "id", "estado", "fecha_inicio", "fecha_fin",
            "frecuencia_pago_id", "nro_cuenta_id", "vehiculo_id"
    };

    /**
     * Crea un contrato y automáticamente:
     * 1. Cambia el estado del vehículo a "alquilado"
     * 2. Crea un pago de garantía
     * 3. Crea pagos diarios por cada día del período del contrato
     */
    public List<String[]> save(String estado,
                               Date fechaInicio,
                               Date fechaFin,
                               int frecuenciaPagoId,
                               int nroCuentaId,
                               int vehiculoId) throws SQLException {

        try (Connection conn = connection.connect()) {
            conn.setAutoCommit(false); // Iniciar transacción

            try {
                // 1. Verificar que el vehículo existe y obtener sus precios
                float precioDia = 0f;
                float montoGarantia = 0f;
                String estadoVehiculo = null;

                String sqlVehiculo = "SELECT precio_dia, monto_garantia, estado FROM \"vehiculos\" WHERE id = ?";
                try (PreparedStatement psVehiculo = conn.prepareStatement(sqlVehiculo)) {
                    psVehiculo.setInt(1, vehiculoId);
                    try (ResultSet rs = psVehiculo.executeQuery()) {
                        if (rs.next()) {
                            precioDia = rs.getFloat("precio_dia");
                            montoGarantia = rs.getFloat("monto_garantia");
                            estadoVehiculo = rs.getString("estado");
                        } else {
                            throw new SQLException("Vehículo no encontrado: " + vehiculoId);
                        }
                    }
                }

                // Verificar que el vehículo esté disponible
                if (!"disponible".equalsIgnoreCase(estadoVehiculo) &&
                        !"reservado".equalsIgnoreCase(estadoVehiculo)) {   // o "reservado"

                    throw new SQLException(
                            "El vehículo con ID " + vehiculoId +
                                    " no está disponible para contrato. Estado actual: " + estadoVehiculo
                    );
                }
                // 2. Actualizar el estado del vehículo a "alquilado"
                String sqlUpdateVehiculo = "UPDATE \"vehiculos\" SET estado = 'alquilado' WHERE id = ?";
                try (PreparedStatement psUpdateVehiculo = conn.prepareStatement(sqlUpdateVehiculo)) {
                    psUpdateVehiculo.setInt(1, vehiculoId);
                    int rowsUpdated = psUpdateVehiculo.executeUpdate();
                    if (rowsUpdated == 0) {
                        throw new SQLException("No se pudo actualizar el estado del vehículo: " + vehiculoId);
                    }
                    System.out.println("✅ Vehículo ID " + vehiculoId + " marcado como alquilado");
                }

                // 3. Crear el contrato
                String sqlContrato = "INSERT INTO \"contratos\" " +
                        "(estado, fecha_inicio, fecha_fin, frecuencia_pago_id, nro_cuenta_id, vehiculo_id) " +
                        "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";

                int contratoId;
                try (PreparedStatement psContrato = conn.prepareStatement(sqlContrato)) {
                    psContrato.setString(1, estado);
                    psContrato.setDate(2, fechaInicio);
                    psContrato.setDate(3, fechaFin);
                    psContrato.setInt(4, frecuenciaPagoId);
                    psContrato.setInt(5, nroCuentaId);
                    psContrato.setInt(6, vehiculoId);

                    try (ResultSet rs = psContrato.executeQuery()) {
                        if (rs.next()) {
                            contratoId = rs.getInt(1);
                            System.out.println("✅ Contrato creado con ID: " + contratoId);
                        } else {
                            throw new SQLException("No se pudo obtener el ID del contrato creado");
                        }
                    }
                }

                // 4. Crear el pago de garantía
                String sqlPagoGarantia = "INSERT INTO \"pagos\" " +
                        "(desde, fecha, hasta, estado, monto, tipo_pago, reserva_id, contrato_id) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement psPagoGarantia = conn.prepareStatement(sqlPagoGarantia)) {
                    psPagoGarantia.setDate(1, fechaInicio);
                    psPagoGarantia.setDate(2, fechaInicio); // fecha de pago = fecha inicio
                    psPagoGarantia.setDate(3, fechaInicio);
                    psPagoGarantia.setString(4, "pendiente");
                    psPagoGarantia.setFloat(5, montoGarantia);
                    psPagoGarantia.setString(6, "garantia");
                    psPagoGarantia.setObject(7, null); // No está asociado a ninguna reserva
                    psPagoGarantia.setInt(8, contratoId); // ASIGNAR CONTRATO_ID

                    psPagoGarantia.executeUpdate();
                    System.out.println("✅ Pago de garantía creado: $" + montoGarantia);
                }

                // 5. Crear los pagos diarios
                LocalDate inicio = fechaInicio.toLocalDate();
                LocalDate fin = fechaFin.toLocalDate();

                // Calcular número de días (inclusive)
                long numeroDias = ChronoUnit.DAYS.between(inicio, fin) + 1;
                System.out.println("📅 Período de alquiler: " + numeroDias + " días (" + inicio + " a " + fin + ")");

                String sqlPagoDiario = "INSERT INTO \"pagos\" " +
                        "(desde, fecha, hasta, estado, monto, tipo_pago, reserva_id, contrato_id) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement psPagoDiario = conn.prepareStatement(sqlPagoDiario)) {
                    LocalDate fechaActual = inicio;
                    int pagosCreados = 0;

                    while (!fechaActual.isAfter(fin)) {
                        psPagoDiario.setDate(1, Date.valueOf(fechaActual));
                        psPagoDiario.setDate(2, Date.valueOf(fechaActual));
                        psPagoDiario.setDate(3, Date.valueOf(fechaActual));
                        psPagoDiario.setString(4, "pendiente");
                        psPagoDiario.setFloat(5, precioDia);
                        psPagoDiario.setString(6, "contrato");
                        psPagoDiario.setObject(7, null); // No está asociado a ninguna reserva
                        psPagoDiario.setInt(8, contratoId); // ASIGNAR CONTRATO_ID

                        psPagoDiario.executeUpdate();
                        pagosCreados++;
                        fechaActual = fechaActual.plusDays(1);
                    }

                    System.out.println("✅ " + pagosCreados + " pagos diarios creados ($" + precioDia + " c/u)");
                }

                // 6. Ya no es necesario crear registros en contrato_pagos
                // porque el contrato_id ya está asignado directamente en cada pago
                System.out.println("✅ Todos los pagos vinculados directamente al contrato ID " + contratoId);

                // 7. Commit de la transacción
                conn.commit();

                float totalPagos = montoGarantia + (precioDia * numeroDias);
                System.out.println("💰 Resumen financiero:");
                System.out.println("   - Garantía: $" + montoGarantia);
                System.out.println("   - Alquiler (" + numeroDias + " días): $" + (precioDia * numeroDias));
                System.out.println("   - Total: $" + totalPagos);

                // 8. Retornar mensaje de confirmación simple
                List<String[]> resultado = new ArrayList<>();
                resultado.add(new String[]{
                        "✅ CONTRATO REGISTRADO EXITOSAMENTE",
                        "ID: " + contratoId,
                        "Vehículo: " + vehiculoId + " (alquilado)",
                        "Período: " + fechaInicio + " a " + fechaFin,
                        "Pagos creados: " + (numeroDias + 1) + " (" + numeroDias + " diarios + 1 garantía)",
                        "Total a pagar: $" + totalPagos
                });

                return resultado;

            } catch (SQLException e) {
                conn.rollback(); // Rollback en caso de error
                System.err.println("❌ Error al crear contrato, realizando rollback: " + e.getMessage());
                throw e;
            } finally {
                conn.setAutoCommit(true); // Restaurar auto-commit
            }
        }
    }

    /**
     * Actualiza solo el estado de un contrato. Si se finaliza o cancela, libera el vehículo.
     */
    public List<String[]> update(int id, String nuevoEstado) throws SQLException {

        try (Connection conn = connection.connect()) {
            conn.setAutoCommit(false);

            try {
                // Primero obtener el vehiculo_id del contrato
                int vehiculoId = -1;
                String sqlGetVehiculo = "SELECT vehiculo_id FROM \"contratos\" WHERE id = ?";
                try (PreparedStatement psGet = conn.prepareStatement(sqlGetVehiculo)) {
                    psGet.setInt(1, id);
                    try (ResultSet rs = psGet.executeQuery()) {
                        if (rs.next()) {
                            vehiculoId = rs.getInt("vehiculo_id");
                        } else {
                            throw new SQLException("Contrato no encontrado: " + id);
                        }
                    }
                }

                // Si el contrato se finaliza o cancela, liberar el vehículo
                if ("finalizado".equalsIgnoreCase(nuevoEstado) || "cancelado".equalsIgnoreCase(nuevoEstado)) {
                    String sqlUpdateVehiculo = "UPDATE \"vehiculos\" SET estado = 'disponible' WHERE id = ?";
                    try (PreparedStatement psUpdateVehiculo = conn.prepareStatement(sqlUpdateVehiculo)) {
                        psUpdateVehiculo.setInt(1, vehiculoId);
                        psUpdateVehiculo.executeUpdate();
                        System.out.println("✅ Vehículo ID " + vehiculoId + " liberado (estado: disponible)");
                    }
                }

                // Actualizar solo el estado del contrato
                String sql = "UPDATE \"contratos\" SET estado = ? WHERE id = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, nuevoEstado);
                    ps.setInt(2, id);
                    if (ps.executeUpdate() == 0) {
                        throw new SQLException("Error al actualizar estado del contrato.");
                    }
                }

                conn.commit();
                System.out.println("✅ Estado del contrato ID " + id + " actualizado a: " + nuevoEstado);

                // Retornar el contrato actualizado usando los headers originales
                return getContratoData(id, conn);

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    /**
     * Elimina un contrato y libera el vehículo asociado.
     */
    public List<String[]> delete(int id) throws SQLException {
        try (Connection conn = connection.connect()) {
            conn.setAutoCommit(false);

            try {
                // Primero obtener el vehiculo_id del contrato que se va a eliminar
                int vehiculoId = -1;
                String sqlGetVehiculo = "SELECT vehiculo_id FROM \"contratos\" WHERE id = ?";
                try (PreparedStatement psGet = conn.prepareStatement(sqlGetVehiculo)) {
                    psGet.setInt(1, id);
                    try (ResultSet rs = psGet.executeQuery()) {
                        if (rs.next()) {
                            vehiculoId = rs.getInt("vehiculo_id");
                        }
                    }
                }

                // Eliminar el contrato
                String sql = "DELETE FROM \"contratos\" WHERE id = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, id);
                    if (ps.executeUpdate() == 0) {
                        throw new SQLException("Error al eliminar contratos.");
                    }
                }

                // Liberar el vehículo si se encontró
                if (vehiculoId != -1) {
                    String sqlUpdateVehiculo = "UPDATE \"vehiculos\" SET estado = 'disponible' WHERE id = ?";
                    try (PreparedStatement psUpdateVehiculo = conn.prepareStatement(sqlUpdateVehiculo)) {
                        psUpdateVehiculo.setInt(1, vehiculoId);
                        psUpdateVehiculo.executeUpdate();
                        System.out.println("✅ Vehículo ID " + vehiculoId + " liberado tras eliminar contrato");
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
        String sql = "SELECT * FROM \"contratos\"";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("estado"),
                        rs.getDate("fecha_inicio").toString(),
                        rs.getDate("fecha_fin").toString(),
                        String.valueOf(rs.getInt("frecuencia_pago_id")),
                        String.valueOf(rs.getInt("nro_cuenta_id")),
                        String.valueOf(rs.getInt("vehiculo_id"))
                });
            }
        }
        return list;
    }

    public void disconnect() {
        connection.closeConnection();
    }
}