// File: negocio/NUserContrato.java
package negocio;

import data.DUserContrato;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NUserContrato {

    private final DUserContrato dao;

    public NUserContrato() {
        this.dao = new DUserContrato();
    }

    public ArrayList<String[]> list() throws SQLException {
        return (ArrayList<String[]>) dao.list();
    }

    public List<String[]> get(int id) throws SQLException {
        return dao.get(id);
    }

    public List<String[]> save(List<String> params) throws SQLException {
        // params: 0=user_id, 1=contrato_id
        int userId     = Integer.parseInt(params.get(0));
        int contratoId = Integer.parseInt(params.get(1));
        return dao.save(userId, contratoId);
    }

    public List<String[]> update(List<String> params) throws SQLException {
        // params: 0=id, 1=user_id, 2=contrato_id
        int id         = Integer.parseInt(params.get(0));
        int userId     = Integer.parseInt(params.get(1));
        int contratoId = Integer.parseInt(params.get(2));
        return dao.update(id, userId, contratoId);
    }

    public List<String[]> delete(List<String> params) throws SQLException {
        // params: 0=id
        int id = Integer.parseInt(params.get(0));
        return dao.delete(id);
    }
}
