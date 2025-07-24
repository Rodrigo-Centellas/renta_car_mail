// File: negocio/NUser.java
package negocio;

import data.DUser;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NUser {

    private final DUser dUser;

    public NUser() {
        this.dUser = new DUser();
    }

    public ArrayList<String[]> list() throws SQLException {
        return (ArrayList<String[]>) dUser.list();
    }

    public List<String[]> get(int id) throws SQLException {
        return dUser.get(id);
    }

    public List<String[]> save(List<String> params) throws SQLException {
        // params: 0=apellido,1=ci,2=domicilio,3=nombre,4=telefono
        String apellido  = params.get(0);
        int    ci        = Integer.parseInt(params.get(1));
        String domicilio = params.get(2);
        String nombre    = params.get(3);
        int    telefono  = Integer.parseInt(params.get(4));
        return dUser.save(apellido, ci, domicilio, nombre, telefono);
    }

    public List<String[]> update(List<String> params) throws SQLException {
        // params: 0=id,1=apellido,2=ci,3=domicilio,4=nombre,5=telefono
        int    id        = Integer.parseInt(params.get(0));
        String apellido  = params.get(1);
        int    ci        = Integer.parseInt(params.get(2));
        String domicilio = params.get(3);
        String nombre    = params.get(4);
        int    telefono  = Integer.parseInt(params.get(5));
        return dUser.update(id, apellido, ci, domicilio, nombre, telefono);
    }

    public List<String[]> delete(List<String> params) throws SQLException {
        // params: 0=id
        int id = Integer.parseInt(params.get(0));
        return dUser.delete(id);
    }
}
