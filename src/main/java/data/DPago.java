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

/**
 * DAO de la tabla <code>pagos</code>.
 * <br/>Sólo se permite modificar el <code>estado</code> mediante <code>update()</code>.
 */
public class DPago {

    public static final String[] HEADERS = {
            "id", "desde", "fecha", "hasta", "estado",
            "pagofacil_transaction_id", "monto", "tipo_pago", "reserva_id", "metodo_pago"
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

    /* ──────────────────────────── READ ──────────────────────────── */

    /** Recupera un pago por ID */
    public List<String[]> get(int id) throws SQLException {
        List<String[]> result = new ArrayList<>();
        String sql = "SELECT * FROM pagos WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result.add(mapRow(rs));
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
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    /* ──────────────────────────── CREATE ──────────────────────────── */

    /**
     * Inserta un nuevo pago, calcula el monto a partir de la reserva (si existe)
     * y marca la reserva como PAGADO.
     */
    public List<String[]> save(
            Date desde,
            Date fecha,
            Date hasta,
            String estado,
            String tipoPago,
            String pagofacilTransactionId,
            Integer reservaId,
            String metodoPago // ⭐ NUEVO PARÁMETRO
    ) throws SQLException {

        try (Connection conn = connection.connect()) {

            /* 1) Calcular monto */
            float montoCalc = calcularMonto(conn, desde, hasta, reservaId);

            /* 2) Insertar pago */
            int newId;
            String ins = "INSERT INTO pagos "
                    + "(desde, fecha, hasta, estado, monto, tipo_pago, "
                    + " pagofacil_transaction_id, reserva_id, metodo_pago) " // ⭐ AGREGAR metodo_pago
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id"; // ⭐ AGREGAR ?

            try (PreparedStatement ps = conn.prepareStatement(ins)) {
                ps.setDate(1, desde);
                ps.setDate(2, fecha);
                ps.setDate(3, hasta);
                ps.setString(4, estado);
                ps.setFloat(5, montoCalc);
                ps.setString(6, tipoPago);

                if (pagofacilTransactionId != null && !pagofacilTransactionId.isEmpty()) {
                    ps.setString(7, pagofacilTransactionId);
                } else {
                    ps.setNull(7, Types.VARCHAR);
                }

                if (reservaId != null) {
                    ps.setInt(8, reservaId);
                } else {
                    ps.setNull(8, Types.INTEGER);
                }

                ps.setString(9, metodoPago); // ⭐ AGREGAR ESTA LÍNEA

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("No se pudo recuperar ID del pago.");
                    }
                    newId = rs.getInt(1);
                }
            }

            /* 3) Marcar reserva como pagada (si aplica) */
            if (reservaId != null) {
                marcarReservaPagada(conn, reservaId);
            }

            /* 4) Devolver pago creado */
            return get(newId);
        }
    }
    /* ──────────────────────────── UPDATE ──────────────────────────── */

    /**
     * Actualiza sólo el <code>estado</code> de un pago.
     * Si el nuevo estado es <em>pagado</em>, marca también la reserva asociada.
     */
    public List<String[]> update(int id, String estado, String metodoPago) throws SQLException { // ⭐ AGREGAR metodoPago

        try (Connection conn = connection.connect()) {

            /* 1) Actualizar campos estado y metodo_pago */
            String up = "UPDATE pagos SET estado = ?, metodo_pago = ? WHERE id = ?"; // ⭐ AGREGAR metodo_pago
            try (PreparedStatement ps = conn.prepareStatement(up)) {
                ps.setString(1, estado);
                ps.setString(2, metodoPago); // ⭐ AGREGAR ESTA LÍNEA
                ps.setInt(3, id);
                if (ps.executeUpdate() == 0) {
                    throw new SQLException("Pago no encontrado: " + id);
                }
            }

            /* 2) Si quedó pagado → marcar reserva */
            if ("pagado".equalsIgnoreCase(estado)) {
                Integer reservaId = obtenerReservaId(conn, id);
                if (reservaId != null) {
                    marcarReservaPagada(conn, reservaId);
                }
            }

            /* 3) Devolver registro actualizado */
            return get(id);
        }
    }

    /* ──────────────────────────── DELETE ──────────────────────────── */

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

    /* ──────────────────────────── HELPERS ──────────────────────────── */

    private String[] mapRow(ResultSet rs) throws SQLException {
        return new String[]{
                String.valueOf(rs.getInt("id")),
                rs.getDate("desde").toString(),
                rs.getDate("fecha").toString(),
                rs.getDate("hasta").toString(),
                rs.getString("estado"),
                rs.getObject("pagofacil_transaction_id") != null
                        ? rs.getString("pagofacil_transaction_id") : "",
                String.valueOf(rs.getFloat("monto")),
                rs.getString("tipo_pago"),
                rs.getObject("reserva_id") != null
                        ? String.valueOf(rs.getInt("reserva_id")) : "",
                rs.getString("metodo_pago") // ⭐ SOLO ESTO ES NUEVO
        };
    }

    /** Calcula el monto a pagar según la reserva (si existe) */
    private float calcularMonto(Connection conn,
                                Date desde,
                                Date hasta,
                                Integer reservaId) throws SQLException {

        if (reservaId == null) return 0f;

        String q = "SELECT v.precio_dia "
                + "FROM reservas rv "
                + "JOIN vehiculos v ON v.id = rv.vehiculo_id "
                + "WHERE rv.id = ?";

        try (PreparedStatement ps = conn.prepareStatement(q)) {
            ps.setInt(1, reservaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    float precioDia = rs.getFloat("precio_dia");
                    long dias = ChronoUnit.DAYS.between(
                            desde.toLocalDate(), hasta.toLocalDate());
                    if (dias < 1) dias = 1;
                    return precioDia * dias;
                }
            }
        }
        throw new SQLException("No se encontró vehículo para reserva " + reservaId);
    }

    /** Marca la reserva como pagada */
    private void marcarReservaPagada(Connection conn, int reservaId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE reservas SET estado = 'pagado' WHERE id = ?")) {

            ps.setInt(1, reservaId);
            ps.executeUpdate();
        }
    }

    /** Obtiene <code>reserva_id</code> de un pago */
    private Integer obtenerReservaId(Connection conn, int pagoId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT reserva_id FROM pagos WHERE id = ?")) {

            ps.setInt(1, pagoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int rid = rs.getInt("reserva_id");
                    return rs.wasNull() ? null : rid;
                }
            }
        }
        return null;
    }

    public void disconnect() {
        connection.closeConnection();
    }
}
