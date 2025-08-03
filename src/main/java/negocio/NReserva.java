// File: negocio/NReserva.java
package negocio;

import data.DReserva;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NReserva {

    private final DReserva dao;

    public NReserva() {
        this.dao = new DReserva();
    }

    public ArrayList<String[]> list() throws SQLException {
        return (ArrayList<String[]>) dao.list();
    }

    public List<String[]> get(int id) throws SQLException {
        return dao.get(id);
    }

    /**
     * Crea una reserva y automáticamente un pago asociado.
     * Parámetros: estado, vehiculo_id, user_id, fecha (YYYY-MM-DD)
     */
    public List<String[]> save(List<String> params) throws SQLException {
        String estado = params.get(0);
        int vehiculoId = Integer.parseInt(params.get(1));
        int userId = Integer.parseInt(params.get(2));
        Date fecha = Date.valueOf(params.get(3));
        return dao.save(estado, vehiculoId, userId, fecha);
    }

    /**
     * Actualiza solo el estado de una reserva existente.
     * Parámetros: id, estado
     */
    public List<String[]> update(List<String> params) throws SQLException {
        int id = Integer.parseInt(params.get(0));
        String estado = params.get(1);
        return dao.update(id, estado);
    }

    public List<String[]> delete(List<String> params) throws SQLException {
        int id = Integer.parseInt(params.get(0));
        return dao.delete(id);
    }
}