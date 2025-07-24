// File: negocio/NClausula.java
package negocio;

import data.DClausula;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NClausula {

    private final DClausula dClausula;

    public NClausula() {
        this.dClausula = new DClausula();
    }

    public ArrayList<String[]> list() throws SQLException {
        return (ArrayList<String[]>) dClausula.list();
    }

    public List<String[]> get(int id) throws SQLException {
        return dClausula.get(id);
    }

    public List<String[]> save(List<String> params) throws SQLException {
        // params.get(0) = descripcion
        String descripcion = params.get(0);
        return dClausula.save(descripcion);
    }

    public List<String[]> update(List<String> params) throws SQLException {
        // params.get(0) = id, params.get(1) = descripcion
        int id = Integer.parseInt(params.get(0));
        String descripcion = params.get(1);
        return dClausula.update(id, descripcion);
    }

    public List<String[]> delete(List<String> params) throws SQLException {
        // params.get(0) = id

        return dClausula.delete(
                Integer.parseInt(params.get(0))
        ); // id
    }
}
