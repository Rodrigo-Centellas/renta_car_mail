// File: negocio/NContratoPago.java
package negocio;

import data.DContratoPago;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NContratoPago {

    private final DContratoPago dao;

    public NContratoPago() {
        this.dao = new DContratoPago();
    }

    public ArrayList<String[]> list() throws SQLException {
        return (ArrayList<String[]>) dao.list();
    }

    public List<String[]> get(int id) throws SQLException {
        return dao.get(id);
    }

    public List<String[]> save(List<String> params) throws SQLException {
        // params: 0=contrato_id, 1=pago_id
        int contratoId = Integer.parseInt(params.get(0));
        int pagoId     = Integer.parseInt(params.get(1));
        return dao.save(contratoId, pagoId);
    }

    public List<String[]> update(List<String> params) throws SQLException {
        // params: 0=id, 1=contrato_id, 2=pago_id
        int id         = Integer.parseInt(params.get(0));
        int contratoId = Integer.parseInt(params.get(1));
        int pagoId     = Integer.parseInt(params.get(2));
        return dao.update(id, contratoId, pagoId);
    }

    public List<String[]> delete(List<String> params) throws SQLException {
        // params: 0=id
        int id = Integer.parseInt(params.get(0));
        return dao.delete(id);
    }
}
