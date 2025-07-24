// File: negocio/NContratoClausula.java
package negocio;

import data.DContratoClausula;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NContratoClausula {

    private final DContratoClausula dao;

    public NContratoClausula() {
        this.dao = new DContratoClausula();
    }

    public ArrayList<String[]> list() throws SQLException {
        return (ArrayList<String[]>) dao.list();
    }

    public List<String[]> get(int id) throws SQLException {
        return dao.get(id);
    }

    public List<String[]> save(List<String> params) throws SQLException {
        // params: 0=contrato_id, 1=clausula_id
        int contratoId = Integer.parseInt(params.get(0));
        int clausulaId = Integer.parseInt(params.get(1));
        return dao.save(contratoId, clausulaId);
    }

    public List<String[]> update(List<String> params) throws SQLException {
        // params: 0=id, 1=contrato_id, 2=clausula_id
        int id         = Integer.parseInt(params.get(0));
        int contratoId = Integer.parseInt(params.get(1));
        int clausulaId = Integer.parseInt(params.get(2));
        return dao.update(id, contratoId, clausulaId);
    }

    public List<String[]> delete(List<String> params) throws SQLException {
        // params: 0=id
        int id = Integer.parseInt(params.get(0));
        return dao.delete(id);
    }
}
