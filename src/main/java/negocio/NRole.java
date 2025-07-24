// File: negocio/NRole.java
package negocio;

import data.DRole;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NRole {

    private final DRole dRole;

    public NRole() {
        this.dRole = new DRole();
    }

    public ArrayList<String[]> list() throws SQLException {
        return (ArrayList<String[]>) dRole.list();
    }

    public List<String[]> get(int id) throws SQLException {
        return dRole.get(id);
    }

    public List<String[]> save(List<String> params) throws SQLException {
        // params.get(0) = nombre
        String nombre = params.get(0);
        return dRole.save(nombre);
    }

    public List<String[]> update(List<String> params) throws SQLException {
        // params.get(0) = id, params.get(1) = nombre
        int id = Integer.parseInt(params.get(0));
        String nombre = params.get(1);
        return dRole.update(id, nombre);
    }

    public List<String[]> delete(List<String> params) throws SQLException {
        // params.get(0) = id
        int id = Integer.parseInt(params.get(0));
        return dRole.delete(id);
    }
}
