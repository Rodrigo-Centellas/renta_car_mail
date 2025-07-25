// File: data/DPago.java
package data;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

public class DPago {

    // HEADERS reordenados según el orden de la base de datos
    public static final String[] HEADERS = {
            "id", "desde", "fecha", "hasta", "estado",
            "pagofacil_transaction_id", "monto", "tipo_pago", "reserva_id"
    };

    private final SqlConnection connection;

    public DPago() {
        this.connection = new SqlConnection(
                DBConnection.database,
                DBConnection.server,
                DBConnection.port,
                DBConnection.user,
                DBConnection.password
        );
    }

    /** Recupera un pago por su ID */
    public List<String[]> get(int id) throws SQLException {
        List<String[]> result = new ArrayList<>();
        String sql = "SELECT * FROM pagos WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result.add(new String[]{
                            String.valueOf(rs.getInt("id")),
                            rs.getDate("desde").toString(),
                            rs.getDate("fecha").toString(),
                            rs.getDate("hasta").toString(),
                            rs.getString("estado"),
                            rs.getObject("pagofacil_transaction_id") != null
                                    ? rs.getString("pagofacil_transaction_id")
                                    : "",
                            String.valueOf(rs.getFloat("monto")),
                            rs.getString("tipo_pago"),
                            rs.getObject("reserva_id") != null
                                    ? String.valueOf(rs.getInt("reserva_id"))
                                    : ""
                    });
                }
            }
        }
        return result;
    }

    /** Lista todos los pagos */
    public List<String[]> list() throws SQLException {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT * FROM pagos";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getDate("desde").toString(),
                        rs.getDate("fecha").toString(),
                        rs.getDate("hasta").toString(),
                        rs.getString("estado"),
                        rs.getObject("pagofacil_transaction_id") != null
                                ? rs.getString("pagofacil_transaction_id")
                                : "",
                        String.valueOf(rs.getFloat("monto")),
                        rs.getString("tipo_pago"),
                        rs.getObject("reserva_id") != null
                                ? String.valueOf(rs.getInt("reserva_id"))
                                : ""
                });
            }
        }
        return list;
    }

    /** Inserta un nuevo pago, calcula monto y marca reserva pagada */
    public List<String[]> save(
            Date desde,
            Date fecha,
            Date hasta,
            String estado,
            String tipoPago,
            String pagofacilTransactionId,
            Integer reservaId
    ) throws SQLException {
        try (Connection conn = connection.connect()) {
            // 1) Calcular monto
            float montoCalc = 0f;
            if (reservaId != null) {
                String q = "SELECT v.precio_dia "
                        + "FROM reservas rv "
                        + " JOIN vehiculos v ON v.id = rv.vehiculo_id "
                        + "WHERE rv.id = ? LIMIT 1";
                try (PreparedStatement ps1 = conn.prepareStatement(q)) {
                    ps1.setInt(1, reservaId);
                    try (ResultSet rs1 = ps1.executeQuery()) {
                        if (rs1.next()) {
                            float precioDia = rs1.getFloat("precio_dia");
                            long dias = ChronoUnit.DAYS.between(
                                    desde.toLocalDate(),
                                    hasta.toLocalDate()
                            );
                            if (dias < 1) dias = 1;
                            montoCalc = precioDia * dias;
                        } else {
                            throw new SQLException(
                                    "No se encontró vehículo para reserva " + reservaId
                            );
                        }
                    }
                }
            }

            // 2) Insertar pago
            String ins = "INSERT INTO pagos "
                    + "(desde, fecha, hasta, estado, monto, tipo_pago, pagofacil_transaction_id, reserva_id) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
            int newId;
            try (PreparedStatement ps2 = conn.prepareStatement(ins)) {
                ps2.setDate(1, desde);
                ps2.setDate(2, fecha);
                ps2.setDate(3, hasta);
                ps2.setString(4, estado);
                ps2.setFloat(5, montoCalc);
                ps2.setString(6, tipoPago);
                if (pagofacilTransactionId != null && !pagofacilTransactionId.isEmpty()) {
                    ps2.setString(7, pagofacilTransactionId);
                } else {
                    ps2.setNull(7, Types.VARCHAR);
                }
                if (reservaId != null) {
                    ps2.setInt(8, reservaId);
                } else {
                    ps2.setNull(8, Types.INTEGER);
                }
                try (ResultSet rs2 = ps2.executeQuery()) {
                    if (!rs2.next()) {
                        throw new SQLException("No se pudo recuperar ID del pago.");
                    }
                    newId = rs2.getInt(1);
                }
            }

            // 3) Marcar reserva como pagada
            if (reservaId != null) {
                String upd = "UPDATE reservas SET estado = 'pagado' WHERE id = ?";
                try (PreparedStatement ps3 = conn.prepareStatement(upd)) {
                    ps3.setInt(1, reservaId);
                    ps3.executeUpdate();
                }
            }

            // 4) Devolver el pago creado
            return get(newId);
        }
    }

    /** Actualiza un pago, recalcula monto y marca reserva */
    public List<String[]> update(
            int id,
            Date desde,
            Date fecha,
            Date hasta,
            String estado,
            String tipoPago,
            String pagofacilTransactionId,
            Integer reservaId
    ) throws SQLException {
        try (Connection conn = connection.connect()) {
            // Repetimos lógica de cálculo
            float montoCalc = 0f;
            if (reservaId != null) {
                String q = "SELECT v.precio_dia "
                        + "FROM reservas rv "
                        + " JOIN vehiculos v ON v.id = rv.vehiculo_id "
                        + "WHERE rv.id = ? LIMIT 1";
                try (PreparedStatement ps1 = conn.prepareStatement(q)) {
                    ps1.setInt(1, reservaId);
                    try (ResultSet rs1 = ps1.executeQuery()) {
                        if (rs1.next()) {
                            float precioDia = rs1.getFloat("precio_dia");
                            long dias = ChronoUnit.DAYS.between(
                                    desde.toLocalDate(),
                                    hasta.toLocalDate()
                            );
                            if (dias < 1) dias = 1;
                            montoCalc = precioDia * dias;
                        } else {
                            throw new SQLException(
                                    "No se encontró vehículo para reserva " + reservaId
                            );
                        }
                    }
                }
            }

            // Update
            String up = "UPDATE pagos SET "
                    + "desde = ?, fecha = ?, hasta = ?, "
                    + "estado = ?, monto = ?, tipo_pago = ?, pagofacil_transaction_id = ?, reserva_id = ? "
                    + "WHERE id = ?";
            try (PreparedStatement ps2 = conn.prepareStatement(up)) {
                ps2.setDate(1, desde);
                ps2.setDate(2, fecha);
                ps2.setDate(3, hasta);
                ps2.setString(4, estado);
                ps2.setFloat(5, montoCalc);
                ps2.setString(6, tipoPago);
                if (pagofacilTransactionId != null && !pagofacilTransactionId.isEmpty()) {
                    ps2.setString(7, pagofacilTransactionId);
                } else {
                    ps2.setNull(7, Types.VARCHAR);
                }
                if (reservaId != null) {
                    ps2.setInt(8, reservaId);
                } else {
                    ps2.setNull(8, Types.INTEGER);
                }
                ps2.setInt(9, id);
                if (ps2.executeUpdate() == 0) {
                    throw new SQLException("Pago no encontrado: " + id);
                }
            }

            // Marcar reserva
            if (reservaId != null) {
                String upd2 = "UPDATE reservas SET estado = 'pagado' WHERE id = ?";
                try (PreparedStatement ps3 = conn.prepareStatement(upd2)) {
                    ps3.setInt(1, reservaId);
                    ps3.executeUpdate();
                }
            }

            return get(id);
        }
    }

    /** Elimina un pago */
    public List<String[]> delete(int id) throws SQLException {
        String sql = "DELETE FROM pagos WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Error al eliminar pago.");
            }
        }
        return list();
    }

    public void disconnect() {
        connection.closeConnection();
    }
}