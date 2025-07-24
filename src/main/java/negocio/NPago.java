// File: negocio/NPago.java
package negocio;

import data.DPago;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NPago {

    private final DPago dao;

    public NPago() {
        this.dao = new DPago();
    }

    /**
     * Devuelve la lista de todos los pagos.
     */
    public ArrayList<String[]> list() throws SQLException {
        return (ArrayList<String[]>) dao.list();
    }

    /**
     * Devuelve un pago por su ID.
     */
    public List<String[]> get(int id) throws SQLException {
        return dao.get(id);
    }

    /**
     * Crea un nuevo pago. Parámetros esperados:
     *   0 = desde      (YYYY-MM-DD)
     *   1 = fecha      (YYYY-MM-DD)
     *   2 = hasta      (YYYY-MM-DD)
     *   3 = estado     (p.ej. "PENDIENTE")
     *   4 = tipo_pago  (p.ej. "EFECTIVO")
     *   5 = reserva_id (opcional, puede estar vacío)
     */
    public List<String[]> save(List<String> params) throws SQLException {
        Date desde       = Date.valueOf(params.get(0));
        Date fecha       = Date.valueOf(params.get(1));
        Date hasta       = Date.valueOf(params.get(2));
        String estado    = params.get(3);
        String tipoPago  = params.get(4);
        Integer reservaId = null;
        if (params.size() > 5 && !params.get(5).isEmpty()) {
            reservaId = Integer.valueOf(params.get(5));
        }
        return dao.save(desde, fecha, hasta, estado, tipoPago, reservaId);
    }

    /**
     * Modifica un pago existente. Parámetros esperados:
     *   0 = id          (ID del pago)
     *   1 = desde       (YYYY-MM-DD)
     *   2 = fecha       (YYYY-MM-DD)
     *   3 = hasta       (YYYY-MM-DD)
     *   4 = estado      (p.ej. "PAGADO")
     *   5 = tipo_pago   (p.ej. "TRANSFERENCIA")
     *   6 = reserva_id  (opcional, puede estar vacío)
     */
    public List<String[]> update(List<String> params) throws SQLException {
        int id            = Integer.parseInt(params.get(0));
        Date desde        = Date.valueOf(params.get(1));
        Date fecha        = Date.valueOf(params.get(2));
        Date hasta        = Date.valueOf(params.get(3));
        String estado     = params.get(4);
        String tipoPago   = params.get(5);
        Integer reservaId = null;
        if (params.size() > 6 && !params.get(6).isEmpty()) {
            reservaId = Integer.valueOf(params.get(6));
        }
        return dao.update(id, desde, fecha, hasta, estado, tipoPago, reservaId);
    }

    /**
     * Elimina un pago existente. Parámetros esperados:
     *   0 = id (ID del pago)
     */
    public List<String[]> delete(List<String> params) throws SQLException {
        int id = Integer.parseInt(params.get(0));
        return dao.delete(id);
    }
}
