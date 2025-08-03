// File: negocio/NPago.java
package negocio;

import data.DPago;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Capa de negocio para la entidad <code>Pago</code>.
 * <br/>Sólo permite actualizar el <code>estado</code> de un pago ya creado.
 */
public class NPago {

    private final DPago dao;

    public NPago() {
        this.dao = new DPago();
    }

    /* ──────────────────────────── READ ──────────────────────────── */

    public ArrayList<String[]> list() throws SQLException {
        return (ArrayList<String[]>) dao.list();
    }

    public List<String[]> get(int id) throws SQLException {
        return dao.get(id);
    }

    /* ──────────────────────────── CREATE ──────────────────────────── */

    /**
     * Crea un pago.
     * <br/>Parámetros esperados:<br/>
     * 0 = desde (YYYY‑MM‑DD)<br/>
     * 1 = fecha (YYYY‑MM‑DD)<br/>
     * 2 = hasta (YYYY‑MM‑DD)<br/>
     * 3 = estado<br/>
     * 4 = tipo_pago<br/>
     * 5 = pagofacil_transaction_id (opcional)<br/>
     * 6 = reserva_id (opcional)
     */
    public List<String[]> save(List<String> params) throws SQLException {
        // params: 0=desde, 1=fecha, 2=hasta, 3=estado, 4=tipo_pago, 5=pagofacil_transaction_id, 6=reserva_id, 7=metodo_pago
        Date desde = Date.valueOf(params.get(0));
        Date fecha = Date.valueOf(params.get(1));
        Date hasta = Date.valueOf(params.get(2));
        String estado = params.get(3);
        String tipoPago = params.get(4);

        String pagofacilTransactionId = null;
        if (params.size() > 5 && !params.get(5).isEmpty()) {
            pagofacilTransactionId = params.get(5);
        }

        Integer reservaId = null;
        if (params.size() > 6 && !params.get(6).isEmpty()) {
            reservaId = Integer.valueOf(params.get(6));
        }

        // ⭐ NUEVO: Manejo del campo metodo_pago
        String metodoPago = "ninguno"; // Valor por defecto
        if (params.size() > 7 && !params.get(7).isEmpty()) {
            metodoPago = params.get(7);
        }

        return dao.save(desde, fecha, hasta, estado, tipoPago, pagofacilTransactionId, reservaId, metodoPago); // ⭐ AGREGAR metodoPago
    }

    /* ──────────────────────────── UPDATE ──────────────────────────── */

    /**
     * Actualiza sólo el estado de un pago.
     * <br/>Parámetros esperados:<br/>
     * 0 = id<br/>
     * 1 = estado
     */
    public List<String[]> update(List<String> params) throws SQLException {
        // params: 0=id, 1=estado, 2=metodo_pago
        int id = Integer.parseInt(params.get(0));
        String estado = params.get(1);

        // ⭐ NUEVO: Manejo del campo metodo_pago
        String metodoPago = "ninguno"; // Valor por defecto
        if (params.size() > 2 && !params.get(2).isEmpty()) {
            metodoPago = params.get(2);
        }

        return dao.update(id, estado, metodoPago); // ⭐ AGREGAR metodoPago
    }

    /* ──────────────────────────── DELETE ──────────────────────────── */

    /**
     * Elimina un pago.
     * <br/>Parámetro esperado: 0 = id
     */
    public List<String[]> delete(List<String> params) throws SQLException {
        int id = Integer.parseInt(params.get(0));
        return dao.delete(id);
    }
}
